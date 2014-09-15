package com.protomapper.App
import java.io.File
import com.protomapper.update._
import scala.io.Source
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
import com.protomapper.search._
import com.protomapper.compile._
import org.clapper.argot._
import org.clapper.argot.ArgotConverters._

object mainapp extends App {
  val parser = new ArgotParser("Protomapper-Search", preUsage=Some("Version 0.1a"))
  val query = parser.option[String]( List("q","query"), "querystring", "pattern to search" )
  val complexity = parser.flag[Boolean]( List("c","complexity"), "get query complexity" )
  val benchmark = parser.option[String]( List("b","benchmerk"), "qstringfile", "file of query strings" )
  val index = parser.option[String]( List("i","index"), "path", "index path" )
  val indexLen = parser.option[Int]( List("l","length"), "l", "index k-gram length" )
  val addseqs = parser.multiOption[String]( List("a","add"), "fastapath dbname", "fasta path and desired name for adding to index" )
  parser.parse(args)
  index.value match {
    case Some(ixpath) => {
      indexLen.value match {
        case Some(ixlen) => {
        //Process remaining options

        println("Creating/Loading Index...")
        val dir = new NIOFSDirectory(new File(ixpath))
        println("Accessing Index...")
        val access = new LuceneAccess(dir,ixlen.toInt,1000000)
        val parser = new PatternParser
	val compiler = new PatternCompiler(parser,ixlen)
        val search = new Searcher(compiler,access)
        benchmark.value match {
          case Some(benchmarklist) => {
            for(i <- Source.fromFile(benchmarklist).getLines()){
              val t0 = System.nanoTime
              val res = search.search(i)
              val t = (System.nanoTime - t0)/1e6
              println(s"${i}\t${compiler.complexity(i)}\t${res.nResults}\t${t}")
              } 
            }
          case None => println("no benchmark file given")
          }
          query.value match {
            case Some(qstring) => {
              //query the index, return results as fasta to stdout
              complexity.value match {
                case Some(flag) => println(compiler.complexity(qstring))
                case None => {
	          val res = search.search(qstring)
	          val (fasta_it,reader) = res.getFastaStream()
                  for(i<-fasta_it)
                    println(i)
                  reader.close()
                }
              }
            }
            case None => {
              addseqs.value match {
                case fastapath:Seq[String] => {
                  //add fasta to index
                  val parser = new SeqParser()
                  if(fastapath.length > 0)
                    access.createIndex(new File(fastapath(0)),fastapath(1))
                }
                case null => println("invalid options")
              }
            }
          }
        }
        case None => println("no index length provided")
      }
    }
    case None => println("No index given")
  }
}

