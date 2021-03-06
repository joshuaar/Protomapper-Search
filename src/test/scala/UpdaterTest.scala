package com.protomapper.test
import org.scalatest.FunSuite
import java.io.File
import com.protomapper.update._
import org.biojava3.core.sequence.ProteinSequence
import org.apache.lucene.store.Directory
import org.apache.lucene.store.RAMDirectory
import org.apache.lucene.search.PhraseQuery
import org.apache.lucene.search.MultiPhraseQuery
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.BooleanClause
import org.apache.lucene.index.Term
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.TopScoreDocCollector
import org.apache.lucene.store.NIOFSDirectory
import com.protomapper.update._
import java.io.StringReader
import org.apache.lucene.analysis.Token.TokenAttributeFactory
import org.apache.lucene.analysis.tokenattributes._
import org.apache.lucene.analysis.ngram.NGramTokenizer
import org.apache.lucene.analysis.core.WhitespaceAnalyzer
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.ngram.NGramTokenFilter




object TestGlobals {
  val ixPath_old = new File("/media/josh/cdb5cb89-333d-4496-a750-b7911cfa70ba/luc_ix2")
  val seqPath_old = new File("/home/josh/CIM/Research/labdata/jaricher/newDecipher/Data for Database/Proteins_Genomes/BactAndVir")
  val ixPath = new File("/media/josh/cdb5cb89-333d-4496-a750-b7911cfa70ba/luc_ix5_1mer")
  val seqPath = new File("/media/josh/cdb5cb89-333d-4496-a750-b7911cfa70ba/uniprot_trembl.fasta")
}

class FalciparumTest extends FunSuite {
  //HELPER FUNCTION
  def createIndex(index:Directory,directory:File) = {
    val parser = new SeqParser()
    val access = new LuceneAccess(index)
    def addToIndex(x:Types.seqs) = {
      access.addSeqs(x,"custom")
      println(s"Added ${x.size()} seqs to DB")
    }
    parser.crawlDirectory(directory, addToIndex)
  }
  test("Add Falciparum") {
    expect(true){
      val ix = new NIOFSDirectory(TestGlobals.ixPath)
      createIndex(ix,new File("/home/josh/CIM/Research/labdata/jaricher/newDecipher/Data for Database/Proteins_Genomes/Plasmodium_falciparum"))
      true
    }
  }
}

class GetIndexedTermsTest extends FunSuite {
  test("Get Organisms Indexed") {
      val access = new LuceneAccess(new NIOFSDirectory(TestGlobals.ixPath))
      for(i <-access.getIndexedTerms("org"))
        println(i)
  }
}

class TokenStreamTest extends FunSuite {
  test("Test NGramAnalyzer") {
    expect(3392) {
      val access = new LuceneAccess(new NIOFSDirectory(TestGlobals.ixPath))
      val y = new File("src/test/scala/testFastas/Dengue.fasta")
      val parser = new SeqParser()
      val seqs = parser.fromFile(y) //  
      val doc = access.createDoc(seqs.values().toArray()(0).asInstanceOf[ProteinSequence], "test")
      val tk = doc.getField("seq").tokenStream(access.analyzer_ng)
      val analyze = new WhitespaceAnalyzer(Version.LUCENE_40)
      val tkstream = analyze.tokenStream("Data", new StringReader("TestStringTestStringTestStringTestSTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringtringTestStringTestStringTestStringTestStringTestStringTestSTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringtringTestStringTestStringTestStringTestStringTestStringTestSTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringtringTestStringTestStringTestStringTestStringTestStringTestSTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringtringTestStringTestStringTestStringTestStringTestStringTestSTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringTestStringtringTestStringTestString"))
      //val tk = new NGramTokenFilter(tkstream,3,3)
      tk.reset()
      val offset = tk.addAttribute(classOf[OffsetAttribute])
      val charterm = tk.addAttribute(classOf[CharTermAttribute])
     // val offset = tk.addAttribute(classOf[OffsetAttribute])
      //val charterm = tk.addAttribute(classOf[CharTermAttribute])
      var tokens = ""
      var lastoffset = 0
      while(tk.incrementToken()) {
        val startOffset = offset.startOffset()
        val endOffset = offset.endOffset()
        lastoffset = endOffset
        val term = charterm.toString()
        val appstring = s"${startOffset}_${term}_${endOffset} : "
        tokens = s"${tokens}${appstring}"
      }
      lastoffset
    }
  }
}

class UpdaterTest extends FunSuite {
  def simpleAddAndQuery(index:Directory):String = {
      val y = new File("src/test/scala/testFastas/Dengue.fasta")
      val parser = new SeqParser()
      val seqs = parser.fromFile(y) //
      
      val access = new LuceneAccess(index)
      access.clearIndex()
      access.addSeqs(seqs, "custom")
      //access.commit()
      val pq = new PhraseQuery()
      pq.add(new Term("seq","AVH"),0)
      //pq.add(new Term("seq","VHA"),1)
      val hitsPerPage = 10
      val reader = DirectoryReader.open(index)
      val searcher = new IndexSearcher(reader)
      val collector = TopScoreDocCollector.create(hitsPerPage,true)
      searcher.search(pq, collector);
      val hits = collector.topDocs().scoreDocs
      var outStr = ""
      for (i <- hits) {
        val docId = i.doc
        val d = searcher.doc(docId)
        outStr += s"${d.get("org")}\t${d.get("acc")}"
      }
      //access.close()
      return outStr
  }
  ignore("Test Updater::addSeqs") {
    expect("Dengue virus 1	NP_059433.1Dengue virus 2	NP_059433.2") {
    	simpleAddAndQuery(new RAMDirectory())
    }
  }
  test("Test Updater::query") {
    expect("0 1 1 1 2") {
      val y = new File("src/test/scala/testFastas/Dengue.fasta")
      val parser = new SeqParser()
      val seqs = parser.fromFile(y) //
      
      val access = new LuceneAccess(new RAMDirectory())
      access.clearIndex()
      access.addSeqs(seqs, "custom")
      //access.commit()
      val pq = new PhraseQuery()
      pq.add(new Term("seq","AVH"),0)
      val res1 = access.query(pq,0,1).scoreDocs
      val res2 = access.query(pq,1,2).scoreDocs
      val res3 = access.query(pq).scoreDocs
      s"${res1(0).doc} ${res1.length} ${res2(0).doc} ${res2.length} ${res3.length}"
    }
  }
  test("Test FS Index") {
    expect("Dengue virus 1	NP_059433.1Dengue virus 2	NP_059433.2") {
    	simpleAddAndQuery(new NIOFSDirectory(new File("index/test.ix")))
    }
  }
}

/*
 *  TESTS BASIC QUERYING OF LARGE INDEX
 */

class CreateLargeIndex extends FunSuite {
  //HELPER FUNCTION
  def createIndex(index:Directory,directory:File) = {
    val parser = new SeqParser()
    val access = new LuceneAccess(index)
    access.clearIndex()
    val writer = access.getWriter()
    def addToIndex(x:Types.seqs) = {
      access.addSeqs(x,"custom",writer)
      println(s"Added ${x.size()} seqs to DB")
    }
    parser.crawlDirectory(directory, addToIndex)
    writer.commit()
    writer.close()
    //access.commit()
    //access.close()
  }
  //TEST STARTS HERE
  test("Create Large Index") {
    expect(true){
      val ix = new NIOFSDirectory(TestGlobals.ixPath)
      createIndex(ix,TestGlobals.seqPath_old)
      true
    }
  }
}

class CreateLargeIndexFromLargeFile extends FunSuite {
  //HELPER FUNCTION
  def createIndex(index:Directory,directory:scala.io.Source) = {
    val parser = new SeqParser()
    val access = new LuceneAccess(index)
    access.clearIndex()
    val writer = access.getWriter()
    var count = 0
    def addToIndex(x:Types.seqs) = {
      access.addSeqs(x,"trembl",writer)
      count+=1
    }
    parser.crawlIterator(directory.getLines, addToIndex)
    writer.commit()
    writer.close()
    println(s"Added ${count} seqs to DB")
    //access.commit()
    //access.close()
  }
  //TEST STARTS HERE
  test("Create Large Index from File") {
    expect(true){
      val ix = new NIOFSDirectory(TestGlobals.ixPath)
      createIndex(ix,scala.io.Source.fromFile(TestGlobals.seqPath))
      true
    }
  }
}

class QueryLargeIndex extends FunSuite {
    //HELPER FUNCTION
    def simpleQuery(index:Directory):Int = {
      
      val access = new LuceneAccess(index)
      val pq = new MultiPhraseQuery()
      pq.add(Array(new Term("seq","AVH")),0)
      pq.add(Array(new Term("seq","VHA")),1)
      pq.add(Array(new Term("seq","HAD"),new Term("seq","HAE"),new Term("seq","HAW")),2)
      val hitsPerPage = 10
      val reader = DirectoryReader.open(index)
      val searcher = new IndexSearcher(reader)
      val collector = TopScoreDocCollector.create(hitsPerPage,true)
      searcher.search(pq, collector);
      val hits = collector.topDocs().scoreDocs
      var outStr = ""
      for (i <- hits) {
        val docId = i.doc
        val d = searcher.doc(docId)
        outStr += s"${d.get("org")}\t${d.get("acc")}"
        println(d.get("org"))
      }
//      /access.close()
      return collector.getTotalHits()
    }
    
    //TEST STARTS HERRE
    ignore("Query Large Index") {
      expect(true) {
        val ix = new NIOFSDirectory(TestGlobals.ixPath)
        simpleQuery(ix)
      }
    }
}