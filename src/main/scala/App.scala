package com.protomapper.App
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
import org.apache.lucene.store.NIOFSDirectory
import com.protomapper.update._

object MmlAlnApp {
  val usage = """
    Usage: java -jar Protomapper-Search.jar --index path [--add path dbname] [--search q1[, q2...]] [--merge mergemethod]
    Descriptions:
    --index:	Location of the index. This must be specified
    --add:		A path of a fasta file to be added to the index
    --search:	A query
    --merge:	Merge method (prots or orgs are accepted, prots by default)
  """
  def main_holder(args: Array[String]) {
    if (args.length == 0) println(usage)
    val arglist = args.toList
    type OptionMap = Map[Symbol, Any]

    def nextOption(map : OptionMap, list: List[String]) : OptionMap = {
      def isSwitch(s : String) = (s(0) == '-')
      list match {
        case Nil => map
        case "--max-size" :: value :: tail =>
                               nextOption(map ++ Map('maxsize -> value.toInt), tail)
        case "--min-size" :: value :: tail =>
                               nextOption(map ++ Map('minsize -> value.toInt), tail)
        case string :: opt2 :: tail if isSwitch(opt2) => 
                               nextOption(map ++ Map('infile -> string), list.tail)
        case string :: Nil =>  nextOption(map ++ Map('infile -> string), list.tail)
        case option :: tail => println("Unknown option "+option) 
                               exit(1) 
      }
    }
    val options = nextOption(Map(),arglist)
    println(options)
  }
}

object Main extends App {
  
  def createIndex(index:Directory,directory:File) = {
    val parser = new SeqParser()
    val access = new LuceneAccess(index)
    access.clearIndex()
    var writer = access.getWriter()
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
  
    
  if( args.length < 1 ){
    println("Usage: java -jar Protomapper-Search.jar ixPath [seqPath]")
    exit(0)
  }
  
  if( args.length < 2 ){
    val ixPath = new File(args(0)) // load the index
    val ix = new NIOFSDirectory(ixPath)
  } else {
    val ixPath = new File(args(0)) // load the index and create it
    val seqPath = new File(args(1))
    val ix = new NIOFSDirectory(ixPath)
    createIndex(ix,seqPath)
  }
  println(args.length)
  Console.println("Call: " + (args mkString ", "))
  
}

