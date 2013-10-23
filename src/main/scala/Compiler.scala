package com.protomapper.compile

import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.MultiPhraseQuery;
import org.apache.lucene.index.Term;

/*
 * Creates an abstract syntax tree from input searches
 * Supports ., {\d,\d}, [^X], [XY], ^, |
 * 
 * re -> simpleRe
 * re -> simpleRe (or) re
 * simpleRe -> factor | factor simpleRe
 * factor -> character | . | range | (re)                                                                          
 * character -> nonSpecialChar | anyChar
 * nonSpecialChar -> sigma - set([,],(,),|)
 * anyChar -> a <- sigma
 * range -> [ optCompliment subRanges ]
 * subRanges -> subRange | subRange subRanges
 * subRange -> character | character - character
 * 
 */
class Parser() {
  var parseString = ""
  def parse(str:String) = {
    parseString = str
  }
  def peek():Char = {
    if(parseString.length() > 0){
    	return parseString.head
    }
    else {
      throw new EndOfInput("reached end")
    }
  }
  def next():Char = {
    val ret = parseString.head
    parseString = parseString.tail
    ret
  }
  def eat(check:Char) = {
    val nxt = this.peek()
    if(nxt == check){
      this.next()
    } else {
      throw new TokenException("Expected "+check+" got "+nxt)
    }
  }
  //TODO, IMPLIMENT PARSER, USE PYTHON ONE AS MODEL
}

case class EndOfInput(msg:String) extends Exception
case class TokenException(msg:String) extends Exception


abstract class re
case class Choice(fst:re, snd:re) extends re
case class Factor(fst:re) extends re
case class Concat(fst:re, snd:re) extends re
case class LenRange(frm:Int,to:Int) extends re
case class Range(comp:Boolean, subRanges:subRanges) extends re
case class subRanges(fst:Array[subRange]) extends re
abstract class subRange extends re
case class subRangeChar(fst:Char) extends subRange
case class subRangeMulti(fst:Char,snd:Char) extends subRange




