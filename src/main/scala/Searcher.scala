package com.protomapper.search
import com.protomapper.compile._
import com.protomapper.update._
import org.apache.lucene.search.TopDocs
import org.apache.lucene.search.ScoreDoc
import scala.collection.mutable.Queue
import org.apache.lucene.index.IndexableField
import org.apache.lucene.search.Query;
import scala.util.parsing.json.JSON
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.util.Version
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanClause.Occur
import org.apache.lucene.search.MultiPhraseQuery
import org.apache.lucene.search.PhraseQuery
import org.apache.lucene.index.SlowCompositeReaderWrapper

object SearcherGlobals {
  val maxCache = 5
}

class Searcher(compiler:PatternCompiler,access:LuceneAccess) {
  
  private val cache = new QueryCache(SearcherGlobals.maxCache)
    
  def search(queryString:String):Result = {
    cache.get(queryString) match {
      case Some(result) => {
        return new Result(queryString,None,result,access) //Result creation goes here
      }
      case None => {
        val query = compiler.compile(queryString)
        val res = access.query(query)
        cache.push(queryString,res) // push results into cache
        return new Result(queryString,None,res,access) // Result creation goes here
      }
    }
  }
  
  /*
   * Searches a querystring against a restricted set of organisms given by orgList
   */
  def search(queryString:String,orgList:Traversable[String]):Result = {
    val key = queryString+orgList.mkString(",")
    cache.get(key) match {
      case Some(result) => {
        return new Result(queryString,orgList,result,access) //Result creation goes here
      }
      case None => {
        val query = compiler.compile(queryString)
        val q = new BooleanQuery()
        q.add(query,Occur.MUST)
        q.add(getOrgQuery(orgList),Occur.MUST)
        val res = access.query(q)
        cache.push(key,res) // push results into cache
        return new Result(queryString,orgList,res,access) // Result creation goes here
      }
    }
  }
  
  /*
   * Gets a query on the org field. For use when restricting organism subsets
   */
  private def getOrgQuery(orgNames:Traversable[String]):Query = {
    val q2 = new MultiPhraseQuery()
    q2.add(orgNames.map(a=>new Term("org",a)).toArray)
    q2
  }
  
  /*
   * Search only the organisms field. Uses Lucene's query parser
   */
  def searchOrgs(orgName:String):Result = {
    val q2 = new PhraseQuery()
    q2.add(new Term("org",orgName))
    val res = access.query(q2)
    return new Result("",Array(orgName),res,access)
  }
  
  def getIndexedOrgs(field:String):List[String] = {
    access.getIndexedTerms("org").toList
  }
 
}

class Result(queryString:String,orgList:Traversable[String],queryRes:TopDocs,access:LuceneAccess){
  val nResults = queryRes.scoreDocs.length
  def getQueryString:String = {
    return queryString
  }
  def getOrgList:Traversable[String] = {
    return orgList
  }
  def getTopDocs():TopDocs = {
    val asdasd = queryRes.scoreDocs
    return queryRes
  }
  
  def getFastaStream():(Iterable[String],org.apache.lucene.index.DirectoryReader) = {
    val reader = access.getReader()
    val docIDs = queryRes.scoreDocs
    def formatFasta(docID:ScoreDoc):String = {
      val seq = wrapText(access.getDocs(docID,reader).getField("seq").stringValue())
      val desc = access.getDocs(docID,reader).getField("desc").stringValue()
      s">${desc}\n${seq}\n"
    }
    val ret = docIDs.view.map( a => formatFasta(a) )
    (ret.toIterable,reader)
  }
  
  /**
   * Wraps text to a certain number of chars
   */
  private def wrapText(in:String, n:Int=80):String = {
    val wrapRegex = s"""(.{1,${n}})""".r
    wrapRegex.findAllIn(in).mkString("\n")
  }
  
  def getJSON(from:Int,to:Int):String= {
    val x = access.getDocs(queryRes.scoreDocs.slice(from,to)) // i/o to get documents
    def renderDoc(doc:org.apache.lucene.document.Document):String = {
      val ret = doc.getFields().toArray().map( (x) => s""" "${x.asInstanceOf[IndexableField].name()}":"${x.asInstanceOf[IndexableField].stringValue()}" """ )
      return "{"+ret.mkString(",")+"}"
    }
    val ret = x.map( (y) => renderDoc(y) )
    return s""" { "num":${nResults},"orgs":[${orgList.map(a=>s""""${a}"""").mkString(",")}],"rng":[${from},${to}],"query":"${queryString}","res":${"["+ret.mkString(",")+"]"} } """
  }
  
  def getMatchingSeqs(from:Int,to:Int):Array[String] = {
    val x = access.getDocs(queryRes.scoreDocs.slice(from,to)) // i/o to get documents
    val out = x.map( (x) => x.get("seq") )
    out
  }
  
  def getOrgNames():Array[String] = {
    var scoredocs = queryRes.scoreDocs
    //THIS IS DUCT TAPE!! FIX THIS RIGHT WITH A CACHE!
    //if(scoredocs.length > 4000)
    //  scoredocs = queryRes.scoreDocs.slice(0,4000)
    val x = access.getDocs(scoredocs)
    val out = x.map( (x) => x.get("org") )
    out
  }
  
  def getUniqueOrgs():scala.collection.mutable.Set[String] = {
    val scoredocs = queryRes.scoreDocs
    val outSet = scala.collection.mutable.Set[String]()
    val reader = access.getReader()
    for(i <- scoredocs){
      outSet += access.getDocs(i,reader).get("org")
      //println(outSet)
    }
    reader.close()
    outSet
  }
  
  def countOrgs():Map[String,Int] = {
    val orgs = getOrgNames()
    val orgsfst = orgs.map( (x) => x.split(" ")(0) )
    val out = orgsfst.groupBy( l => l).map( t => (t._1, t._2.length))
    out
  }
  
  def countOrgsJSON():String = {
    val counts = countOrgs()
    val l = counts.map( (x) => s""""${x._1}":${x._2}""" )
    l.tail.foldLeft("{")( (x,y) =>  x+y+"," ) + l.head + "}"
  }
  
  def mergeOrgs(otherResult:Result):Result = {
    val orgs1=getOrgNames()
    val orgs2=otherResult.getOrgNames()
    val set1 = orgs1.toSet[String]
    val set2 = orgs2.toSet[String]
    val setboth = set1.intersect(set2)
    val outorgs1 = for (i <- orgs1.zipWithIndex if set2.contains(i._1)) yield queryRes.scoreDocs(i._2)
    val outorgs2 = for (i <- orgs2.zipWithIndex if set1.contains(i._1)) yield otherResult.getTopDocs.scoreDocs(i._2)
    val newres = new TopDocs(outorgs1.length+outorgs2.length,outorgs1 ++ outorgs2,otherResult.getTopDocs.getMaxScore())
    return new Result(queryString,None,newres,access)
  }
  
}

/**
 * Simple cache for storing recently searched results in memory
 * Parameters:
 * max:Int - Max number of queries stored in cache
 */
class QueryCache(max:Int) {
  
  var cached = Map[String,TopDocs]()
  var keyQueue = Queue[String]()
  
  def push(queryString:String, result:TopDocs) = {
    cached += (queryString -> result)
    keyQueue.enqueue(queryString)
    if(keyQueue.length > max){ // remove an item from cache if it gets too big
      val key = keyQueue.dequeue
      cached = cached - key
    }
  }  
  def isCached(queryString:String):Boolean = {
    cached.contains(queryString)
  }
  
  def get(queryString:String):Option[TopDocs] = {
    cached.get(queryString)
  }
}
