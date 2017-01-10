package boot;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import analyzers.CustomAnalyzerFactory;
import model.Article;
import model.RespArticles;
import utils.NewsBootUtils;

public class BootNewsAPI {
	static String YOUR_APIKEY;
	
	public static void main(String[] args) throws IOException {		
		Set<String> enSourcesIDs = NewsBootUtils.getSourcesIDs();
		int i = 0;
		for(String id : enSourcesIDs){
			i++;
			NewsBootUtils.getAllArticlesFromSource(id, i);
		}
	}
	
	private static Set<String> tokenize(String text) throws IOException {
		CustomAnalyzer analyzer = CustomAnalyzerFactory.buildTweetAnalyzer();
		TokenStream stream = analyzer.tokenStream("field", text);

		CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);
		Set<String> tags = new HashSet<String>();
		try {
			stream.reset();
			while (stream.incrementToken()) {
				//System.out.println(termAtt.toString());
				tags.add(termAtt.toString());
			}
			stream.end();
			return tags;
		} finally {
			stream.close();
			analyzer.close();
		}
	}
	
}