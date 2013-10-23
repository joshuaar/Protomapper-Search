package main.test.scala.testFastas
import org.scalatest.FunSuite
import java.io.File
import com.protomapper.update._
import org.biojava3.core.sequence.ProteinSequence
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.search.PhraseQuery
import org.apache.lucene.search.MultiPhraseQuery
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.BooleanClause
import org.apache.lucene.index.Term;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopScoreDocCollector;




class UpdaterTest extends FunSuite {
  test("Test Updater::addSeqs") {
    expect("Dengue virus 1	NP_059433.1Dengue virus 2	NP_059433.2") {
      val y = new File("src/test/scala/testFastas/Dengue.fasta")
      val parser = new SeqParser()
      val seqs = parser.fromFile(y) //
      
      val index = new RAMDirectory()
      val access = new LuceneAccess(index,3)
      access.addSeqs(seqs, "custom")
      access.writer.close()
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
      outStr
    }
  }
}