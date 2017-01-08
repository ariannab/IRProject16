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
			getAllArticlesFromSource(id, i);
		}
	}

	private static void getAllArticlesFromSource(String sourceID, int j) throws MalformedURLException, IOException, ProtocolException {
		YOUR_APIKEY = NewsBootUtils.loadAPIKey();
		
		System.out.println("\n\n--------------------- Source "+j+": "+sourceID+" -------------------------");
		URL obj = new URL("https://newsapi.org/v1/articles?source="+sourceID+"&apiKey="+YOUR_APIKEY);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		int responseCode = con.getResponseCode();
		System.out.println("GET Response Code :: " + responseCode);
		
		if (responseCode == HttpURLConnection.HTTP_OK) { 
			// success
			StringBuffer response = NewsBootUtils.obtainResponse(con);			
			String prettyJsonString = NewsBootUtils.formatJson(response.toString());
//			System.out.println(prettyJsonString);
			RespArticles resp = NewsBootUtils.buildRespPOJO(prettyJsonString);
			List<Article> articles = resp.getArticles();
//			for (Article a : articles)
//				System.out.println(a);
			
			int i=0;
			for(Article a : articles){
				try {
					i++;
					System.out.println("\nArticle #"+i);
					String title = a.getTitle();
					if(title!=null)
						a.getTags().addAll(tokenize(title));
					String desc = a.getDescription();
					if(desc!=null)
						a.getTags().addAll(tokenize(desc));
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println(a.getTags().toString());
			}				
		} else {
			System.out.println("GET request not worked");
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