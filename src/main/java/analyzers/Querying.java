package analyzers;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
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

public class Querying {
	private static CustomAnalyzer analyzer;

	public static void makeQuery(Path userIndex, Path articlesIndex, CustomAnalyzer extAnalyz) throws IOException {
		analyzer = extAnalyz;
		Directory dir = FSDirectory.open(userIndex);
		// initialize the index reader
		DirectoryReader reader = DirectoryReader.open(dir);
		Document doc = reader.document(0);
		BooleanQuery.setMaxClauseCount(10000000);
		Builder qBuilder = new BooleanQuery.Builder();
		
		qBuilder = addTokensForQuery(doc, "utags", qBuilder, (float) 0.7);
		qBuilder = addTokensForQuery(doc, "ftags", qBuilder, (float) 0.3);

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
			if (art.getField("title") != null) {
				atitle = art.getField("title").stringValue();
			}
			System.out.println("title: " + atitle + " -- Score: " + score);
		}

		
	}

	private static Builder addTokensForQuery(Document doc, String fieldName, Builder qBuilder, float boost) throws IOException {
		IndexableField ufield = doc.getField(fieldName);
		TokenStream stream =ufield.tokenStream(analyzer, null);
		CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);

		try {
			stream.reset();
			while (stream.incrementToken()) {
				Query qTerm = new TermQuery(new Term("atags", termAtt.toString()));
				BoostQuery boostQ = new BoostQuery(qTerm, boost);				
				qBuilder.add(boostQ, BooleanClause.Occur.SHOULD);
			}
			stream.end();
		} finally {
			stream.close();
		}
		return qBuilder;
	}

}
