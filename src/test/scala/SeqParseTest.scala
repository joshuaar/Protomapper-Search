package main.test.scala.testFastas
import org.scalatest.FunSuite
import java.io.File
import com.protomapper.update._
import org.biojava3.core.sequence.ProteinSequence

class SeqParseTest extends FunSuite {
  val nProts = 18866
  test("Test SeqParser::fromFile") {
    expect(nProts) {
      val x = new File("src/test/scala/testFastas/Borrellia.fasta")
      val y = new File("src/test/scala/testFastas/Dengue.fasta")
      val z = new File("src/test/scala/testFastas/hiv1.fasta")
      val parser = new SeqParser()
      val j = parser.fromFile(x)
      val k = parser.fromFile(y)
      val l = parser.fromFile(z)
      j.size()+k.size()+l.size()
      //j.keySet().size()
    }
  }
  test("Test SeqParser::fromDirectory") {
    expect(nProts) {
      val x = new File("src/test/scala/testFastas/")
      val parser = new SeqParser()
      val j = parser.fromDirectory(x)
      j.size()
    }
  }
  test("Test SeqParser::crawlDirectory") {
    expect(nProts) {
      val x = new File("src/test/scala/testFastas/")
      val parser = new SeqParser()
      var count = 0
      def f(x:Types.seqs) = {
        count += x.size()
      }
      parser.crawlDirectory(x,f)
      count
    }
  }
  test("Test Manipulate Fasta") {
    expect(nProts) {
      val x = new File("src/test/scala/testFastas/")
      val parser = new SeqParser()
      val j = parser.fromDirectory(x)
      val seqs = j.values()
      val res = seqs.toArray.map((x) => x.asInstanceOf[ProteinSequence].getOriginalHeader())
      res.size
    }
  }
}