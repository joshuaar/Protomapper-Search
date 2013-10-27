package com.protomapper.compile

import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.MultiPhraseQuery;
import org.apache.lucene.search.BooleanClause.Occur

/*
 * Classes related to parsing and compiling
 */
abstract class re
case class Choice(fst:re, snd:re) extends re
case class Term(fst:List[re]) extends re
case class Factor(fst:re) extends re
case class Concat(fst:re, snd:re) extends re
case class LenRange(frm:Int,to:Int,base:re) extends re
case class Range(comp:Boolean, subRanges:List[SubRange]) extends re
case class Str(fst:String) extends re
case class Wild extends re
abstract class SubRange extends re
case class SubRangeChar(fst:Char) extends SubRange
case class SubRangeMulti(fst:Char,snd:Char) extends SubRange

object CompilerGlobals {
  val sigma = List[String]("A","C","D","E","F","G","H","I","K","L","M","N","P","Q","R","S","T","U","V","W","Y")
}

/*
 * Creates a query from the abstract syntax tree generated by PatternParser
 */
class PatternCompiler(parser:PatternParser) {
  
  def compile(patt:String):Any = {
    val ast = parser.parse(patt)
    val dechoiced = _dechoice(ast)
    val deLenRanged = dechoiced.map(_deLenRange)
    return deLenRanged.foldRight(List[re]())((x,y) => x ++ y)
  }
  
  //Creates multiPhraseQueries from terms (no BooleanQueries yet)
  
  
  //Walks the parse tree, removing choices in favor of list of expressions
  private def _dechoice(ast:re):List[Term] = {
    ast match {
      case Choice(fst,snd) => {
        return this._dechoice(fst) ++ this._dechoice(snd)
      }
      case t:Term => { //No more choices deeper in this term because sub-expressions are not allowed
        return t :: List[Term]()
      }
    }
  }
  //Gets rid of length ranges in terms
  private def _deLenRange(term:Term):List[re] = {
    var out = List[Term]()
    for(i <- 0 until term.fst.length) {
      term.fst(i) match {
        case LenRange(frm,to,base) => {
          val prefix = term.fst.slice(0,i)
          var len = frm
          for( j <- 0 to to - frm ){
            println(j)
            var newPrefix = prefix
            for(k <- 0 until len){
              newPrefix = newPrefix :+ base
            }
            val newTerm = newPrefix ++ term.fst.slice(i+1,term.fst.length)
            out = Term(newTerm) :: out
            len += 1
          }
        }
        case _ => {
          
        }
      }
    }
    if(out.length == 0){
    	out = term :: out
    }
    return out
  }
}

/*
 * Generates windows from a debooleanized and deLenRanged ast
 */
class WindowGen(len:Int,ast:Term) {
  var tokensLeft = ast.fst

  println(tokensLeft)
  if(tokensLeft.length < len)
    throw new TokenException("Query is too short")
  
  //Get the next set of tokens
  def next():List[String] = {
    println (windowsLeft())
    if(windowsLeft() <= 0){
      return List[String]()
    } else {
      val cur = tokensLeft.head //The seed for the fold operation
      val tk = tokensLeft.slice(0,len).map((x) => expand(x)) //The thing to be folded
      tokensLeft = tokensLeft.tail // trim down the remaining tokens
      return tk.slice(0,len - 1).foldRight(tk(len-1))(this.cross) // Do the cross product to get a list of strings
    }
  }
  
  def windowsLeft():Int = {
    return tokensLeft.length - len + 1
  }
  
  //Gives the cross product of two args
  private def cross(fst:List[String],snd:List[String]):List[String] = {
    return for { i <- fst; j <- snd } yield i+j
  }
  
  private def expand(fst:re):List[String] = {
    val fstStr = fst match {
      case w:Wild => {
        wildExpand()
      }
      case r:Range => {
        rangeExpand(r)
      }
      case Str(s) => {
        List[String](s)
      }
    }
    return fstStr
  }
  
  private def rangeExpand(ast:Range):List[String] = {
    var out = List[String]()
    for{i <- ast.subRanges} {
      i match {
        case SubRangeChar(char) => 
          out = char.toString() :: out
        case SubRangeMulti(a,b) =>
          throw new TokenException("Multi Ranges unsupported")
      }
    }
    if(ast.comp) {
      out = for {i <- CompilerGlobals.sigma if !(out contains i)} yield i
    }
    return out
  }
  
  private def wildExpand():List[String] = {
    return CompilerGlobals.sigma
  }
}

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
class PatternParser() {
  var parseString = ""
    val specialChars = "()*?|[]{}+"
    val EOFChar = ';'
  parseString = parseString.replaceAll(" ","")//remove whitespace
  
  def parse(str:String):re = {
    parseString = str
    this.regex()
  }
  
  def peek():Char = {
    //println("peek: "+parseString)
    if(parseString.length() > 0){
    	return parseString.head
    }
    else {
      return EOFChar
    }
  }
  
  def next():Char = {
    //println("next: "+parseString)
    val ret = parseString.head
    parseString = parseString.tail
    //println("post-next: "+parseString)
    ret
  }
  def eat(check:Char) = {
    //println("eat: "+parseString)
    val nxt = this.peek()
    if(nxt == check){
      this.next()
    } else {
      throw new TokenException("Expected "+check+" got "+nxt)
    }
  }
  def more():Boolean = {
    return parseString.length() > 0
  }
  
  def regex():re = {
    //println("regex")
    val term = this.term()
    if(more() & peek() == '|') {
      eat('|')
      return Choice(term,regex())
    }
    else {
      return term
    }
  }
  
  def term():re = {
    //println("term")
    var fac = List[re]()
    while(more() & peek() != '|' & peek() != ')'){
      val nxt = factor()
      fac = fac :+ nxt //O(n) complexity, should be optimized someday
    }
    Term(fac)
  }
  def factor():re = {
    //println("factor")
    val base = this.base()
    //println("factor2")
    while(more() & peek() == '{'){
      //println("factor::{")
      eat('{')
      val lenRng = this.lenRng(base)
      eat('}')
      return lenRng
    }
    return base
  }
  def lenRng(base:re):LenRange = {
    val regex = """(\d+),(\d+).*""".r
    //println("lenRange")
    parseString match {
      case regex(from,to) => {
        //println("matches")
        val nChars = from.length()+to.length()+1 //digits+comma
        for(i <- 0 until nChars)
          next()
        return LenRange(from.toInt,to.toInt,base)
      }
    }
    throw new TokenException("Length Range Invalid")
  }
  
  def rng():Range = {
    var comp = false;
    if(!more()) {
      throw new TokenException("Unexpected end of input")
    }
    if(peek() == '^'){
      comp = true
      eat('^')
    }
    var subRanges = List[SubRange]()
    while( more() & ! (specialChars contains peek()) ){
      subRanges = this.subRng()::subRanges
    }
    return Range(comp,subRanges)
  }
  
  def subRng():SubRange = {
    val nxt = peek()
    val regex = """(\w)-(\w).*""".r
    parseString match {
      case regex(w1,w2) => {
        next()
        next()
        next()
        return SubRangeMulti(w1(0),w2(0))
      }
      case _ => {
        if(!(specialChars contains nxt)){
          return SubRangeChar(next())
        } else {
          throw new TokenException("Unexpected Character in sub range")
        }
      }
    }
  }
  def base():re = {
    //println("base")
    val nxt = peek()
    var out = "";
    if(nxt == '(') {
      //println("base::(")
      throw new TokenException("Sub-expressions disabled")
      eat('(')
      val r = this.regex()
      //println("base::post(")
      eat(')')
      return r
    } else if(nxt == '[') {
      eat('[')
      val rng = this.rng()
      eat(']')
      return rng
    } else {
      //println(s"base::else ${!(specialChars contains peek())}")
      if( more() & !(specialChars contains peek()) ){
        //println("base::else::if1")
        out += next()
      }
      if( out.length() > 0 ){
        //println(s"base::else::if2 -- ${more()} -- ${out.length}")
        if(out != ".") {
        	return Str(out)         
        } else {
        	return Wild()
        }
      }
      throw new TokenException(s"Unexpected Token ${peek()}")
    }
  }
}

case class EndOfInput(msg:String) extends Exception
case class TokenException(msg:String) extends Exception






