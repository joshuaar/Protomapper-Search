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

object Types {
	type seqs = LinkedHashMap[String,ProteinSequence]
}
//Parses sequences from file or web
class SeqParser() {
  def fromFile(in:File):LinkedHashMap[String,ProteinSequence] = {
    val out = FastaReaderHelper.readFastaProteinSequence(in)
    return out
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