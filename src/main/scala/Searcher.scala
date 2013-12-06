package com.protomapper.search
import com.protomapper.compile._
import com.protomapper.update._
import org.apache.lucene.search.TopDocs
import scala.collection.mutable.Queue

object SearcherGlobals {
  val maxCache = 5
}

class Searcher(compiler:PatternCompiler,access:LuceneAccess) {
  
  private val cache = new QueryCache(SearcherGlobals.maxCache)
  
  def search(queryString:String):TopDocs = {
    cache.get(queryString) match {
      case Some(result) => {
        return result //Result creation goes here
      }
      case None => {
        val query = compiler.compile(queryString)
        val res = access.query(query)
        cache.push(queryString,res) // push results into cache
        res // Result creation goes here
      }
    }
  }
}

/**
 * Simple cache for storing recently searched results in memory
 * Parameters:
 * max:Int - Max number of queries stored in cache
 */
class QueryCache(max:Int) {
  
  var cached = Map[String,TopDocs]()
  var keyQueue = Queue[String]()
  
  def push(queryString:String, result:TopDocs) = {
    cached += (queryString -> result)
    keyQueue.enqueue(queryString)
    if(keyQueue.length > max){ // remove an item from cache if it gets too big
      val key = keyQueue.dequeue
      cached = cached - key
    }
  }
  
  def isCached(queryString:String):Boolean = {
    cached.contains(queryString)
  }
  
  def get(queryString:String):Option[TopDocs] = {
    cached.get(queryString)
  }
}