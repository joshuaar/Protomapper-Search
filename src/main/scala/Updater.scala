package com.protomapper.update
import java.io.StringReader
import java.io.IOException;
import java.text.ParseException;
import java.io.Reader;
import org.biojava3.core.sequence.ProteinSequence;
import org.apache.lucene.analysis.Analyzer.TokenStreamComponents;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.lucene.search.Collector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.search.RegexpQuery;
import org.apache.lucene.index.Term;
import org.apache.lucene.analysis.ngram.NGramTokenFilter
import org.apache.lucene.analysis.ngram.NGramTokenizer
import org.apache.lucene.search.PhraseQuery
import org.apache.lucene.search.MultiPhraseQuery
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.BooleanClause
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper
import org.apache.lucene.analysis.standard.StandardAnalyzer
import java.util.HashMap


//Analyzer for peptide sequences
class NGramAnalyzer(minGram:Int,maxGram:Int) extends Analyzer {
  @Override
  protected def createComponents(arg0:String, reader:Reader):TokenStreamComponents = {
    val source = new NGramTokenizer(reader,minGram,maxGram)
    val filter = new NGramTokenFilter(source,minGram,maxGram)
    return new TokenStreamComponents(source,filter)
  }
}

//Configuration options for LuceneAccess
object LuceneAccess {
  val nMerLen = 3
  val maxHits = 1000000
}

//The class for updating and searching lucene index
class LuceneAccess(index:Directory) {
  //Set up analyzers, Standard for all fields but seq
  val analyzer_ng = new NGramAnalyzer(LuceneAccess.nMerLen,LuceneAccess.nMerLen)
  val analyzer_field = new StandardAnalyzer(Version.LUCENE_40)
  val analyzerMap = new HashMap[String,Analyzer]()
  analyzerMap.put("seq",analyzer_ng)
  val analyzer = new PerFieldAnalyzerWrapper(analyzer_field,analyzerMap)
  //Set up config and writer
  val config = new IndexWriterConfig(Version.LUCENE_40,analyzer_ng)
  
  
  //var writer = new IndexWriter(index,config)
  //val reader = DirectoryReader.open(index)
  //val searcher = new IndexSearcher(reader)
  //Adds some document to store, doesnt care about structure
  private def addDoc(doc:Document) {
    var writer = new IndexWriter(index,config)
    writer.addDocument(doc)
    writer.commit()
    writer.close()
  }
  
  def getWriter():IndexWriter = {
    return new IndexWriter(index,config)
  }
  
  private def addDoc(doc:Document, writer:IndexWriter) {
    writer.addDocument(doc)
  }
  
  def getDocs(docs:Array[ScoreDoc]):Array[Document] = {
    val reader = DirectoryReader.open(index)
    val searcher = new IndexSearcher(reader)
    val out = docs.map( (x) => searcher.doc(x.doc) )
    reader.close()
    out
  }
  
  def query(q:Query,begin:Int,end:Int):TopDocs = {
    val collector = TopScoreDocCollector.create(LuceneAccess.maxHits,true)
    val reader = DirectoryReader.open(index)
    val searcher = new IndexSearcher(reader)
    val res = searcher.search(q, collector)
    val out = collector.topDocs(begin,end)
    reader.close()
    return out
  }
  
  def query(q:Query):TopDocs = {
    val collector = TopScoreDocCollector.create(LuceneAccess.maxHits,true)
    val reader = DirectoryReader.open(index)
    val searcher = new IndexSearcher(reader)
    val res = searcher.search(q, collector)
    val out = collector.topDocs()
    reader.close()
    return out
  }
  
  //For creating and adding sequence documents
  def addSeq(seq:String,acc:String,org:String,desc:String,db:String) = {
    val doc = new Document()
    doc.add(new TextField("seq",seq,Field.Store.YES))
    doc.add(new TextField("acc",acc,Field.Store.YES))
    doc.add(new TextField("desc",desc,Field.Store.YES))
    doc.add(new TextField("org",org,Field.Store.YES))
    doc.add(new StringField("db",db,Field.Store.YES))
    //doc.add(new TextField("tags",tags,Field.Store.YES))
    
    //Use this for deduplication
    //val uniqueID = new Term("acc",acc)
    //writer.updateDocument(uniqueID, doc)
    
    //Allow duplicates
    addDoc(doc)
  }
  
    //For creating and adding sequence documents
  def addSeq(seq:String,acc:String,org:String,desc:String,db:String, writer:IndexWriter) = {
    val doc = new Document()
    doc.add(new TextField("seq",seq,Field.Store.YES))
    doc.add(new TextField("acc",acc,Field.Store.YES))
    doc.add(new TextField("desc",desc,Field.Store.YES))
    doc.add(new TextField("org",org,Field.Store.YES))
    doc.add(new StringField("db",db,Field.Store.YES))
    //doc.add(new TextField("tags",tags,Field.Store.YES))
    
    //Use this for deduplication
    //val uniqueID = new Term("acc",acc)
    //writer.updateDocument(uniqueID, doc)
    
    //Allow duplicates
    addDoc(doc, writer)
  }
  
  def addSeq(sequence:ProteinSequence,db:String):Unit = {//,tags:String):Unit = {
    val seq = sequence.getSequenceAsString()
    val accession = sequence.getAccession().toString()
    val desc = sequence.getOriginalHeader()
    val orgPattern = """.*\[(.*)\].*""".r //pattern for getting organisms out of header lines
    val org = desc match {
      case orgPattern(o) => o
      case _ => "undefined"
    }
    addSeq(seq,accession,org,desc,db)
  }
  

  def addSeq(sequence:ProteinSequence,db:String, writer:IndexWriter):Unit = {//,tags:String):Unit = {
    val seq = sequence.getSequenceAsString()
    val accession = sequence.getAccession().toString()
    val desc = sequence.getOriginalHeader()
    val orgPattern = """.*\[(.*)\].*""".r //pattern for getting organisms out of header lines
    val org = desc match {
      case orgPattern(o) => o
      case _ => "undefined"
    }
    addSeq(seq,accession,org,desc,db,writer)
  }
  
  
  def addSeqs(seqs:Types.seqs,db:String):Unit = {
    //Add all the seqs to the lucene database
    seqs.values.toArray().map( (x) => addSeq( x.asInstanceOf[ProteinSequence],db ) )
  }
  
  def addSeqs(writer:IndexWriter, db:String,seqs:Types.seqs) = {
    seqs.values.toArray().map( (x) => addSeq( x.asInstanceOf[ProteinSequence],db,writer ) )
  }
  
  def clearIndex() = {
    var writer = new IndexWriter(index,config)
    writer.deleteAll()
    writer.commit()
    writer.close()
  }
  
}
