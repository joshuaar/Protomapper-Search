package com.protomapper.test
import org.scalatest.FunSuite
import java.io.File
import java.io.PrintWriter
import com.protomapper.compile._
import com.protomapper.update._
import com.protomapper.search._
import com.protomapper.util._
import org.biojava3.core.sequence.ProteinSequence
import org.apache.lucene.store.NIOFSDirectory


class SeqGenTest extends FunSuite {
  val bor = new SeqDist(SeqDist.bordist)
  val array = new SeqDist(SeqDist.arraydist)
  val rand = new SeqDist(SeqDist.rdist)
  val iedb = new SeqDist(SeqDist.iedbdist)
  test("Test Sequence Generation") {
  	expect(5) {

  	  bor.gen(5).length
  	}
  }
  test("Generate and search protein lvl") {
    expect(true){
    val searcher = SearchTestGlobals.getSearcher()
    def predicate(searchString:String):Int = {
      val res = searcher.search(searchString)
      res.getTopDocs.scoreDocs.length
    }
    def predicateOrgs(searchString1:String, searchString2:String):Int = {
      val res1 = searcher.search(searchString1)
      val res2 = searcher.search(searchString2)
      return res1.getUniqueOrgs().intersect(res2.getUniqueOrgs).toList.length
    }
    def searchRandomsProts(n:Int,len1:Int,len2:Int,dist:SeqDist):Array[Int] = {
      val sstring = (1 to n).map( a => SeqDist.genSub(dist.gen(len1)) ++ "^" ++ SeqDist.genSub(dist.gen(len2))).toArray
      sstring.map(predicate)
    }
    def searchRandomsOrgs(n:Int,len1:Int,len2:Int,dist:SeqDist):Array[Int] = {
      val sstring = (1 to n).map( a => (dist.gen(len1),dist.gen(len2))).toArray
      sstring.map( a=> predicateOrgs(a._1,a._2))
    }
    def writeArray(outFileName:String, arr:Array[Int]) = {
      val writer = new PrintWriter(new File(outFileName))
      for(i <- arr){
        writer.write(i.toString ++ "\n")
      }
      writer.close()
    }
    def search80(sdist:SeqDist, name:String, l1:Int, l2:Int,n:Int) = {
    	writeArray("/home/josh/CIM/Research/labdata/jaricher/Papers/Epitopes_Decipher/Analyses/RandomDBSearches/" +
    			s"${name}_l1_${l1}_l2_${l2}_n_1000.txt",searchRandomsProts(n,l1,l2,sdist))
    }
    def search100Orgs(sdist:SeqDist, name:String, l1:Int, l2:Int,n:Int) = {
    	writeArray("/home/josh/CIM/Research/labdata/jaricher/Papers/Epitopes_Decipher/Analyses/RandomDBSearches/" +
    			s"${name}_l1_${l1}_l2_${l2}_n_1000.txt",searchRandomsOrgs(n,l1,l2,sdist))
    }
    for(i <- 4 to 7){
      for(j <- 4 to 7){
        search80(bor,"lifeSpace_80s",i,j,1000)
        search80(array,"arraySpace_80s",i,j,1000)
        search80(rand,"rSpace_80s",i,j,1000)
        search80(iedb,"iedbSpace_80s",i,j,1000)
      }
    }
  }
  }
}