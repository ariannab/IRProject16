package analyzers;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

public class Querying {
	private static CustomAnalyzer analyzer;
	static float uboost = 0.6f;
	static float fboost = 0.4f;
	

	public static void makeQuery(Path userIndex, Path articlesIndex, CustomAnalyzer extAnalyz) throws IOException {
//		final long startTime = System.currentTimeMillis();		
		
		analyzer = extAnalyz;
		Directory dir = FSDirectory.open(userIndex);
		
		// initialize the index reader
		DirectoryReader reader = DirectoryReader.open(dir);
//		Document doc = reader.document(0);
		BooleanQuery.setMaxClauseCount(10000000);
		Builder qBuilder = new BooleanQuery.Builder();		

		Terms uTermVector = reader.getTermVector(0, "utags");
		TermsEnum termIt = uTermVector.iterator();
		qBuilder = addTokensInQuery(termIt, qBuilder, uboost);
		
		Terms fTermVector = reader.getTermVector(0, "ftags");
		termIt = fTermVector.iterator();
		qBuilder = addTokensInQuery(termIt, qBuilder, fboost);
		
		BooleanQuery q = qBuilder.build();		
		
		Directory dir1 = FSDirectory.open(articlesIndex);
		DirectoryReader reader1 = DirectoryReader.open(dir1);
		IndexSearcher artSearcher1 = new IndexSearcher(reader1);
		artSearcher1.setSimilarity(new BM25Similarity());		
		TopDocs topdocs1 = artSearcher1.search(q, 20);
		ScoreDoc[] resultList1 = topdocs1.scoreDocs; 
		System.out.println("BM25 Similarity results: " + topdocs1.totalHits + " - we show top 20");
		
		for (int i = 0; i < resultList1.length; i++) {
			Document art = artSearcher1.doc(resultList1[i].doc);
			float score = resultList1[i].score;
			String atitle = "";
			String asource = "";
			if (art.getField("title") != null) 
				atitle = art.getField("title").stringValue();

			if (art.getField("source") != null) 
				asource = art.getField("source").stringValue();
			System.out.println("	title #"+(i+1)+": <" + atitle + "> source: <"+asource+"> *** Score: " + score);
			
			String filename = "explainations/exp_score_"+(i+1)+".txt";
			PrintWriter out = new PrintWriter(filename);
			out.println(((artSearcher1.explain(q, resultList1[i].doc)).toString()));
			out.close();
		}

//		final long endTime = System.currentTimeMillis();
//		System.out.println("\nTotal execution time: " + (endTime - startTime) );		
		
	}

	private static Builder addTokensInQuery(TermsEnum termIt, Builder qBuilder, float boost) throws IOException {
		
		BytesRef t;
		while((t = termIt.next()) != null){
			String termString = t.utf8ToString();
			float freq = termIt.totalTermFreq();
			float finalBoost = boost * freq;
			
			Query qTerm = new TermQuery(new Term("atags", termString));
			BoostQuery boostQ = new BoostQuery(qTerm, finalBoost);				
			qBuilder.add(boostQ, BooleanClause.Occur.SHOULD);
			
		}
		
		return qBuilder;
		
	}

//	private static Builder addTokensForQuery(Document doc, String fieldName, Builder qBuilder, float boost) throws IOException {
//		IndexableField ufield = doc.getField(fieldName);
//		TokenStream stream =ufield.tokenStream(analyzer, null);
//		CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);
//
//		try {
//			stream.reset();
//			while (stream.incrementToken()) {
//				Query qTerm = new TermQuery(new Term("atags", termAtt.toString()));
//				BoostQuery boostQ = new BoostQuery(qTerm, boost);				
//				qBuilder.add(boostQ, BooleanClause.Occur.SHOULD);
//			}
//			stream.end();
//		} finally {
//			stream.close();
//		}
//		return qBuilder;
//	}

}
