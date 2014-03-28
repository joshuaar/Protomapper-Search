package com.protomapper.update

import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
 
import org.biojava3.core.sequence.ProteinSequence;
import org.biojava3.core.sequence.compound.AminoAcidCompound;
import org.biojava3.core.sequence.compound.AminoAcidCompoundSet;
import org.biojava3.core.sequence.io.FastaReader;
import org.biojava3.core.sequence.io.FastaReaderHelper;
import org.biojava3.core.sequence.io.GenericFastaHeaderParser;
import org.biojava3.core.sequence.io.ProteinSequenceCreator;
import java.io.ByteArrayInputStream

object Types {
	type seqs = LinkedHashMap[String,ProteinSequence]
}
//Parses sequences from file or web
class SeqParser() {
  def fromFile(in:File):LinkedHashMap[String,ProteinSequence] = {
    val out = FastaReaderHelper.readFastaProteinSequence(in)
    return out
  }
  
  //applies function f to every complete fasta in the iterator in
  def crawlIterator(in:Iterator[String], f:Types.seqs=>Any) = {
    var buff = ""
    def readFastaFromString(fasta:String):Types.seqs = {
      val is = new ByteArrayInputStream(fasta.getBytes());
      FastaReaderHelper.readFastaProteinSequence(is)
    }
    def extendBuff():Boolean = {
      var keepGoing = true
      while(in.hasNext && keepGoing){
        val next = in.next
       // println(buff)
        if(!(next.length == 0) && next.charAt(0) == '>') //stop when we reach next line
          keepGoing = false
        buff ++= next + "\n"
      }
      if(in.hasNext)
        return true
      buff += ">"
      return false
    }
    val regex = "(?s)>.*>".r
    def consume():Option[Types.seqs] = {
      val out = regex.findFirstIn(buff) match {
        case Some(fasta) => Some(readFastaFromString(fasta.slice(0,fasta.length-1)))
        case None => None
      }
      buff = regex.replaceFirstIn(buff, ">")

      out
    }
    var keepGoing = true
    while(keepGoing){
      keepGoing = extendBuff()
      consume() match {
        case Some(fasta) => f(fasta)
        case None => // do nothing
      }
    }
    
  }
  
  //Parses sequences by crawling for fasta files in directory
  def fromDirectory(in:File):Types.seqs = {
    in.isDirectory() match {
      case true => {
        val children = in.listFiles()
        def mergeMap(x:Types.seqs,y:Types.seqs):Types.seqs = {
          val out = new Types.seqs()
          out.putAll(x)
          out.putAll(y)
          return out
        }
        //Collect the results of the directory walk
        val out = children.map( (x) => this.fromDirectory(x) ).foldRight( new Types.seqs() )( mergeMap )
        out // return the results of the subdirectory crawl
      }
      case false => {
        val fastaPattern = """.*(\.faa|\.fasta)""".r
        val fName = in.getName()
        val out = fName match {
          case fastaPattern(s) => {
            this.fromFile(in)
            }
          case _ => {
            new Types.seqs()
          }
        }
        out // return the fasta file post parse
      }
    }
  }
  //Apply f to each fasta in the directory
  def crawlDirectory(in:File,f:(Types.seqs) => Unit):Unit = {
    in.isDirectory() match {
      case true => {
        val children = in.listFiles()
        def mergeMap(x:Types.seqs,y:Types.seqs):Types.seqs = {
          val out = new Types.seqs()
          out.putAll(x)
          out.putAll(y)
          return out
        }
        //walk the children
        children.map( (x) => this.crawlDirectory(x,f) )
      }
      case false => {
        val fastaPattern = """.*(\.faa|\.fasta)""".r
        val fName = in.getName()
        val out = fName match {
          case fastaPattern(s) => this.fromFile(in)
          case _ => new Types.seqs()
        }
        f(out)//apply f to the fasta file
      }
    }
  }
}