package analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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

import model.RankingArticle;

public class Querying {

	static float maxUserFreq;
	static float maxFFreq;
	static float uboost;
	
	/**
	 * Build and submit a boolean query to the news index. 
	 * Clauses (connected by OR operators) are user profile's terms 
	 * (user plus friends ones, with different boosts)
	 * 
	 * @param userIndex
	 * @param articlesIndex
	 * @param alwaysTop
	 * @return list of RankingArticle objects representing the query results
	 * @throws Exception
	 */

	public static List<RankingArticle> makeQuery(Path userIndex, Path articlesIndex, boolean alwaysTop) throws Exception {
		Directory userDir = FSDirectory.open(userIndex);

		DirectoryReader uReader = DirectoryReader.open(userDir);

		// we stored the term vector during indexing phase,
		// so we're able to retrieve it now
		Terms uTermVector = uReader.getTermVector(0, "utags");
		Terms fTermVector = uReader.getTermVector(0, "ftags");
		
		//start building the boolean query
		int clauseCount = (int) (uTermVector.size()+fTermVector.size());		
		BooleanQuery.setMaxClauseCount(clauseCount);
		Builder qBuilder = new BooleanQuery.Builder();	
		
		maxUserFreq = Indexing.getHighestFreq(userIndex, "utags");
		maxFFreq = Indexing.getHighestFreq(userIndex, "ftags");
				
		qBuilder = addUserContentInQuery(uTermVector, qBuilder, alwaysTop);
		qBuilder = addFriendsContentInQuery(fTermVector, qBuilder);
		BooleanQuery query = qBuilder.build();		
		
		Directory newsDir = FSDirectory.open(articlesIndex);
		DirectoryReader newsReader = DirectoryReader.open(newsDir);
		IndexSearcher artSearcher = new IndexSearcher(newsReader);
		artSearcher.setSimilarity(new BM25Similarity());
		
		//submit query to the news index
		TopDocs topdocs = artSearcher.search(query, 20);
		ScoreDoc[] resultList = topdocs.scoreDocs; 

//		final long endTime = System.currentTimeMillis();
//		System.out.println("\nTotal execution time: " + (endTime - startTime) );		
		
		List<RankingArticle> result = getQueryResult(query, artSearcher, resultList);	

		uReader.close();
		newsReader.close();
		return result;

		
	}

	/**
	 * Returns the query result list 
	 * 
	 * @param query
	 * @param artSearcher
	 * @param resultList
	 * @return list of RankingArticle objects representing the query results
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private static List<RankingArticle> getQueryResult(BooleanQuery query, IndexSearcher artSearcher, ScoreDoc[] resultList)
			throws IOException, FileNotFoundException {
		List<RankingArticle> result = new ArrayList<RankingArticle>();
		for (int i = 0; i < resultList.length; i++) {
			Document art = artSearcher.doc(resultList[i].doc);
			float score = resultList[i].score;
			String atitle = "";
			String asource = "";
			if (art.getField("title") != null) 
				atitle = art.getField("title").stringValue();

			if (art.getField("source") != null) 
				asource = art.getField("source").stringValue();

//			System.out.println("	title #"+(i+1)+": <" + atitle + "> source: <"+asource+"> *** Score: " + score);
//			result.add("title: <" + atitle + "> source: <"+asource+"> *** Score: " + score + "\n");
			result.add(new RankingArticle(atitle, asource, score));

			new File("explanations/").mkdir();
			String filename = "explanations/exp_score_"+(i+1)+".txt";
			PrintWriter out = new PrintWriter(filename);
			out.println(((artSearcher.explain(query, resultList[i].doc)).toString()));
			out.close();
		}
		
		return result;

	}

	/**
	 * Add friends tokens (clauses) to the boolean query, adjusting the boost 
	 * (in this case, the normalized frequency)
	 * 
	 * @param termV
	 * @param qBuilder
	 * @return the updated query builder
	 * @throws IOException
	 */
	private static Builder addFriendsContentInQuery(Terms termV, Builder qBuilder) throws IOException {		
		BytesRef t;
		TermsEnum termIt = termV.iterator();
		float freq;
				
		while((t = termIt.next()) != null){
			String termString = t.utf8ToString();
			
			//normalization of frequencies (values in 0-1)
			freq = termIt.totalTermFreq()/maxFFreq;
						
			Query qTerm = new TermQuery(new Term("atags", termString));
			BoostQuery boostQ = new BoostQuery(qTerm, freq);				
			qBuilder.add(boostQ, BooleanClause.Occur.SHOULD);			
		}
		
		return qBuilder;		
	}	
	
	/**
	 * Add user tokens (clauses) to the boolean query, adjusting the boost
	 * 
	 * @param termV
	 * @param qBuilder
	 * @param alwaysTop
	 * @return the updated query builder
	 * @throws IOException
	 */
	private static Builder addUserContentInQuery(Terms termV, Builder qBuilder, boolean alwaysTop) throws IOException {		
		BytesRef t;
		TermsEnum termIt = termV.iterator();
		float freq;
		float finalBoost;
				
		while((t = termIt.next()) != null){
			String termString = t.utf8ToString();
			
			//normalization of frequencies (values in 0-1)
			freq = termIt.totalTermFreq()/maxUserFreq;

			//if user content is not "always absolutely on top",
			//his boosting can be of 2 or 3
			finalBoost = (alwaysTop) ? freq + 1 : uboost * freq; 
			
			Query qTerm = new TermQuery(new Term("atags", termString));
			BoostQuery boostQ = new BoostQuery(qTerm, finalBoost);				
			qBuilder.add(boostQ, BooleanClause.Occur.SHOULD);			
		}
		
		return qBuilder;		
	}

	public static void setUboost(float uboost) {
		Querying.uboost = uboost;
	}

}
