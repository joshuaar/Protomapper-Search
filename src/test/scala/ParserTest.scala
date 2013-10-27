package com.protomapper.test
import org.scalatest.FunSuite
import java.io.File
import com.protomapper.compile._
import org.biojava3.core.sequence.ProteinSequence

class WindowGenTest extends FunSuite {
  test("Test WindowGen") {
    expect(true){
    	val parser = new PatternParser
    	val parsed = parser.parse("THEFGH").asInstanceOf[Term]
    	val gen = new WindowGen(3,parsed)
    	gen.next()
    	gen.next()
    }
  }
}

class PatternParserTest extends FunSuite {
  test("Test PatternParser::parse(TEST)") {
    expect("Term(List(Str(T), Str(E), Str(S), Str(T)))") {
      val parse = new PatternParser
      parse.parse("TEST").toString()
    }
  }
  ignore("""Test PatternParser::parse(T(ES)T)""") {
    expect("Term(List(Str(T), Term(List(Str(E), Str(S))), Str(S), Str(T)))") {
      val parse = new PatternParser
      parse.parse("T(ES)ST").toString()
    }
  }
  test("""Test PatternParser::parse(T[ES]T)""") {
    expect("Term(List(Str(T), Range(false,List(SubRangeChar(S), SubRangeChar(E))), Str(S), Str(T)))") {
      val parse = new PatternParser
      parse.parse("T[ES]ST").toString()
    }
  }
  test("""Test PatternParser::parse(T[^ES]ST)""") {
    expect("Term(List(Str(T), Range(true,List(SubRangeChar(S), SubRangeChar(E))), Str(S), Str(T)))") {
      val parse = new PatternParser
      parse.parse("T[^ES]ST").toString()
    }
  }
  test("""Test PatternParser::parse(T{2,4}EST)""") {
    expect("Term(List(LenRange(2,4,Str(T)), Str(E), Str(S), Str(T)))") {
      val parse = new PatternParser
      parse.parse("T{2,4}EST").toString()
    }
  }
  test("""Test PatternParser::parse(T|E|ST)""") {
    expect("Term(List(LenRange(2,4,Str(T)), Str(E), Str(S), Str(T)))") {
      val parse = new PatternParser
      parse.parse("T|E|ST").toString()
    }
  }
  test("""Test PatternParser::parse(T[A-Z]EST)""") {
    expect("Term(List(Str(T), Range(false,List(SubRangeMulti(A,Z))), Str(E), Str(S), Str(T)))") {
      val parse = new PatternParser
      parse.parse("T[A-Z]EST").toString()
    }
  }
  test("""Test PatternParser::parse(T.EST)""") {
    expect("Term(List(Str(T), Wild(), Str(E), Str(S), Str(T)))") {
      val parse = new PatternParser
      parse.parse("T.EST").toString()
    }
  }
  ignore("Test PatternParser::parse(HEL[LO](WO)RLD|!!)") {
    expect("Choice(Term(List(Str(H), Str(E), Str(L), Range(false,List(SubRangeChar(O), SubRangeChar(L))), Term(List(Str(W), Str(O))), Str(R), Str(L), Str(D))),Term(List(Str(!), Str(!))))") {
      val parse = new PatternParser
      parse.parse("HEL[LO](WO)RLD|!!").toString()
    }
  }
}