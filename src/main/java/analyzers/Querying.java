package analyzers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
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
	static float uboost = 0.8f;
	static float fboost = 0.2f;	

	/**
	 * Build and submit a boolean query to the news index. 
	 * Clauses (OR) are user profile's tags 
	 * (user plus friends ones, with different boosts)
	 * 
	 * @param userIndex
	 * @param articlesIndex
	 * @throws IOException
	 */
	public static void makeQuery(Path userIndex, Path articlesIndex) throws IOException {
//		final long startTime = System.currentTimeMillis();		
		
		Directory userDir = FSDirectory.open(userIndex);
		
		// initialize the index reader
		DirectoryReader uReader = DirectoryReader.open(userDir);
		BooleanQuery.setMaxClauseCount(100000);
		Builder qBuilder = new BooleanQuery.Builder();		

		//we stored the term vector during indexing phase,
		//so we're able to retrieve it now
		Terms uTermVector = uReader.getTermVector(0, "utags");
		TermsEnum termIt = uTermVector.iterator();
		qBuilder = addTokensInQuery(termIt, qBuilder, uboost);
		
		Terms fTermVector = uReader.getTermVector(0, "ftags");
		termIt = fTermVector.iterator();
		qBuilder = addTokensInQuery(termIt, qBuilder, fboost);
		
		BooleanQuery query = qBuilder.build();		
		
		Directory newsDir = FSDirectory.open(articlesIndex);
		DirectoryReader newsReader = DirectoryReader.open(newsDir);
		IndexSearcher artSearcher = new IndexSearcher(newsReader);
		artSearcher.setSimilarity(new BM25Similarity());
		
		//submit query to the news index
		TopDocs topdocs = artSearcher.search(query, 20);
		ScoreDoc[] resultList = topdocs.scoreDocs; 
		System.out.println("BM25 Similarity results: " + topdocs.totalHits + " - we show top 20");
		
		printQueryResult(query, artSearcher, resultList);

//		final long endTime = System.currentTimeMillis();
//		System.out.println("\nTotal execution time: " + (endTime - startTime) );		
		
	}

	private static void printQueryResult(BooleanQuery query, IndexSearcher artSearcher, ScoreDoc[] resultList)
			throws IOException, FileNotFoundException {
		for (int i = 0; i < resultList.length; i++) {
			Document art = artSearcher.doc(resultList[i].doc);
			float score = resultList[i].score;
			String atitle = "";
			String asource = "";
			if (art.getField("title") != null) 
				atitle = art.getField("title").stringValue();

			if (art.getField("source") != null) 
				asource = art.getField("source").stringValue();
			System.out.println("	title #"+(i+1)+": <" + atitle + "> source: <"+asource+"> *** Score: " + score);
			
			String filename = "explainations/exp_score_"+(i+1)+".txt";
			PrintWriter out = new PrintWriter(filename);
			out.println(((artSearcher.explain(query, resultList[i].doc)).toString()));
			out.close();
		}
	}

	/**
	 * Add tokens (clauses) to the boolean query, adjusting the boost
	 * 
	 * @param termIt
	 * @param qBuilder
	 * @param boost
	 * @return the updated query builder
	 * @throws IOException
	 */
	private static Builder addTokensInQuery(TermsEnum termIt, Builder qBuilder, float boost) throws IOException {
		
		BytesRef t;
		while((t = termIt.next()) != null){
			String termString = t.utf8ToString();
			float freq = termIt.totalTermFreq();
			//final boost for the term is base boost multiplied by term frequency
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
