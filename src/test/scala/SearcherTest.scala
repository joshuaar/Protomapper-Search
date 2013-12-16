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
    expect(1) {
      val parser = new PatternParser
	  val compiler = new PatternCompiler(parser,3)
	  val ix = new NIOFSDirectory(TestGlobals.ixPath)
	  val access = new LuceneAccess(ix)
	  val search = new Searcher(compiler,access)
	  search.search("AVHAD").countOrgsJSON()
	  1
    }
  }
}
