package com.protomapper.test
import org.scalatest.FunSuite
import java.io.File
import com.protomapper.compile._
import com.protomapper.update._
import com.protomapper.search._
import org.biojava3.core.sequence.ProteinSequence
import org.apache.lucene.store.NIOFSDirectory

class SearchTest extends FunSuite {
  ignore("Test search") {
    expect(true) {
      val parser = new PatternParser
	  val compiler = new PatternCompiler(parser,3)
	  val ix = new NIOFSDirectory(TestGlobals.ixPath)
	  val access = new LuceneAccess(ix)
	  val search = new Searcher(compiler,access)
      val query = "AVHADD[EA]{0,4}"
	  val sres = search.search(query)
	  val res = sres.getMatchingSeqs(0,10)
	  val mch = s".*(${query}).*".r
	  def isMatch(seq:String):Boolean = {
	    seq match {
	      case mch(s) => true
	      case _ => false
	    }
	  }
	  res.map( (x) => isMatch(x) ).reduce( (x,y) => x&&y )
	  //res.map( (x) => x.contains(query) )
    }
  }
}
