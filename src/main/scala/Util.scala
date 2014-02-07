package com.protomapper.util
import scala.util.Random


/*
 * Generate random sequences for significance testing
 */
class SeqGen(lib:Library) {
  def gen(n:Int):String = {
    var out = ""
    for (i <- Range(0,n,1)) {
      out ++ lib.next()
    }
    out
  }
}

class Library {
  def next():String = {
    return ""
  }
}