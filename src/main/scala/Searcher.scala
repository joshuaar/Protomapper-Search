package com.protomapper.search
import com.protomapper.compile._
import com.protomapper.update._
import org.apache.lucene.search.TopDocs
import scala.collection.mutable.Queue
import org.apache.lucene.index.IndexableField
import org.apache.lucene.search.Query;
import scala.util.parsing.json.JSON

object SearcherGlobals {
  val maxCache = 5
}

class Searcher(compiler:PatternCompiler,access:LuceneAccess) {
  
  private val cache = new QueryCache(SearcherGlobals.maxCache)
    
  def search(queryString:String):Result = {
    cache.get(queryString) match {
      case Some(result) => {
        return new Result(queryString,result,access) //Result creation goes here
      }
      case None => {
        val query = compiler.compile(queryString)
        val res = access.query(query)
        cache.push(queryString,res) // push results into cache
        return new Result(queryString,res,access) // Result creation goes here
      }
    }
  }
  
  def searchMultiOrgs(querys:Array[String]):Result = {
    val results = querys.map( (x) => search(x) )
    results.tail.foldLeft(results.head)( (x,y) => x.mergeOrgs(y) )
  }
    
  def search(queryString:String,db:String) = {
    //to be implimented
  }
}

class Result(queryString:String,queryRes:TopDocs,access:LuceneAccess){
  println(queryRes.scoreDocs.length)
  def getQueryString:String = {
    return queryString
  }
  
  def getTopDocs():TopDocs = {
    val asdasd = queryRes.scoreDocs
    return queryRes
  }
  
  def getJSON(from:Int,to:Int):String= {
    val x = access.getDocs(queryRes.scoreDocs.slice(from,to)) // i/o to get documents
    def renderDoc(doc:org.apache.lucene.document.Document):String = {
      val ret = doc.getFields().toArray().map( (x) => s""" "${x.asInstanceOf[IndexableField].name()}":"${x.asInstanceOf[IndexableField].stringValue()}" """ )
      return "{"+ret.mkString(",")+"}"
    }
    val ret = x.map( (y) => renderDoc(y) )
    return s""" { "query":${queryString},"res":${"["+ret.mkString(",")+"]"} } """
  }
  
  def getOrgNames():Array[String] = {
    val x = access.getDocs(queryRes.scoreDocs)
    val out = x.map( (x) => x.get("org") )
    out
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
    l.foldLeft("{")( (x,y) =>  x+y+"," ) + "}"
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
    return new Result(queryString,newres,access)
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