package com.protomapper.test
import org.scalatest.FunSuite
import java.io.File
import com.protomapper.compile._
import com.protomapper.update._
import com.protomapper.search._
import org.biojava3.core.sequence.ProteinSequence
import org.apache.lucene.store.NIOFSDirectory

class SearchTest extends FunSuite {
  def getSearcher():Searcher = {
      val parser = new PatternParser
	  val compiler = new PatternCompiler(parser,3)
	  val ix = new NIOFSDirectory(TestGlobals.ixPath)
	  val access = new LuceneAccess(ix)
	  val search = new Searcher(compiler,access)
      return search
  }
  
	def isMatch(seq:String,query:String):Boolean = {
	  val mch = s".*(${query}).*".r
	  seq match {
	    case mch(s) => true
	    case _ => false
	  }
	}
  test("Test search basic") {
    expect(true) {
      val search = getSearcher()
      val query = "AVHADD[EA]{0,4}"
	  val sres = search.search(query)
	  val res = sres.getMatchingSeqs(0,10)
	  res.map( (x) => isMatch(x,query) ).reduce( (x,y) => x&&y )&& ( res.length == 10 )
	  //res.map( (x) => x.contains(query) )
    }
  }
  test("Test search with Ands") {
    expect(true){
      val search = getSearcher()
      val query = "AVH^ADD"
      val sres = search.search(query)
      val res = sres.getMatchingSeqs(0,10)
      res.map( (x) => isMatch(x,"AVH")&&isMatch(x,"ADD") ).reduce( (x,y) => x&&y )&& ( res.length == 10 )
    }
  }
  test("Test short search (AH)") {
      val search = getSearcher()
      val query = "AH"
	  val sres = search.search(query)
	  val res = sres.getMatchingSeqs(0,10)
	  res.map( (x) => isMatch(x,query) ).reduce( (x,y) => x&&y )&& ( res.length == 10 )
  }
  test("Test short search (A.{1,1})") {
      val search = getSearcher()
      val query = "A.{1,1}"
	  val sres = search.search(query)
	  val res = sres.getMatchingSeqs(0,10)
	  res.map( (x) => isMatch(x,query) ).reduce( (x,y) => x&&y )&& ( res.length == 10 )
  }
}
