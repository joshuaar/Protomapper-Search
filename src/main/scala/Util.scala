package com.protomapper.util
import scala.util.Random


/*
 * Generate random sequences for significance testing
 */
class SeqDist(distribution: Map[String,Int]) {
  val total = distribution.values.sum
  //Picks an item from a random number
  private def weightedPick():String = {
    val basenum = Random.nextInt(total)+1 // random number [1..total]
    var numLeft = basenum
    for(i <- distribution.keys) {
      numLeft -= distribution.get(i).get
      if(numLeft <= 0)
        return i
    }
    ""
  }
  
  def next():String = {
    return weightedPick()
  }
  
  def gen(n:Int):String = {
    var out = ""
    for (i <- Range(0,n,1)) {
      out += next()
    }
    out
  }
}

object SeqDist {
  
  def genSub(queryString:String) = {
    ((0 to queryString.length() -1 ).map(a=>queryString.subSequence(0,a)+"."+queryString.substring(a+1))++List(queryString)).reduce(
    (a,b) => a+"|"+b   
    )
  }

  val bordist = Map[String,Int](
      ("A",17399),
      ("C", 2855),
      ("E", 26899),
      ("D", 20199),
      ("G", 18610),
      ("F", 22616),
      ("I", 39370),
      ("H", 4818),
      ("K", 41235),
      ("M", 7240),
      ("L", 39236),
      ("N", 28909),
      ("Q", 9792),
      ("P", 9262),
      ("S", 28421),
      ("R", 12254),
      ("T", 16214),
      ("W", 1785),
      ("V", 19412),
      ("Y", 16791)
      )
  val arraydist = Map[String,Int](
      ("A", 292140),
      ("E", 284749),
      ("D", 249406),
      ("G", 300283),
      ("F", 179842),
      ("H", 314735),
      ("K", 229280),
      ("L", 233554),
      ("N", 216632),
      ("Q", 140370),
      ("P", 309679),
      ("S", 175336),
      ("R", 289548),
      ("W", 298880),
      ("V", 272133),
      ("Y", 229476)   
      )
  val iedbdist = Map[String,Int](
      ("A", 18306),
      ("C", 2889),
      ("E", 14172),
      ("D", 10642),
      ("G", 14911),
      ("F", 11353),
      ("I", 14548),
      ("H", 5692),
      ("K", 14646),
      ("M", 5795),
      ("L", 24504),
      ("N", 11810),
      ("Q", 10452),
      ("P", 12832),
      ("S", 18091),
      ("R", 13317),
      ("T", 15137),
      ("W", 4166),
      ("V", 17000),
      ("Y", 13064)
      )
  
    val rdist = Map[String,Int](
  ("A", 1),
  ("C", 1),
  ("E", 1),
  ("D", 1),
  ("G", 1),
  ("F", 1),
  ("I", 1),
  ("H", 1),
  ("K", 1),
  ("M", 1),
  ("L", 1),
  ("N", 1),
  ("Q", 1),
  ("P", 1),
  ("S", 1),
  ("R", 1),
  ("T", 1),
  ("W", 1),
  ("V", 1),
  ("Y", 1)
  )
}