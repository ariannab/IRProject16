package boot;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import analyzers.CustomAnalyzerFactory;
import model.Article;
import model.RespArticles;
import model.RespSources;
import model.Source;

public class BootNewsAPI {
	static String YOUR_APIKEY;
	
	public static void main(String[] args) throws IOException {		
		Set<String> enSourcesIDs = getSourcesIDs();
		int i = 0;
		for(String id : enSourcesIDs){
			i++;
			getAllArticlesFromSource(id, i);
		}
	}

	private static Set<String> getSourcesIDs() throws MalformedURLException, IOException, ProtocolException {
		URL obj = new URL("https://newsapi.org/v1/sources?language=en");
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		int responseCode = con.getResponseCode();
		System.out.println("GET Response Code :: " + responseCode);
		
		if (responseCode == HttpURLConnection.HTTP_OK) { 
			StringBuffer response = obtainResponse(con);			
			String prettyJsonString = formatJson(response.toString());
//			System.out.println(prettyJsonString);
			Set<String> sourceIDs=new HashSet<String>();
			RespSources resp = buildSourcePOJO(prettyJsonString);
			for(Source s : resp.getSources()){
				String id = s.getId();
				sourceIDs.add(id);
//				System.out.println("Source id: "+id);
			}
			return sourceIDs;
		}else {
			System.out.println("GET request not worked");
		}
		return null;
	}

	private static RespSources buildSourcePOJO(String prettyJsonString) {
		Gson gson = new Gson();
		RespSources resp = gson.fromJson(prettyJsonString, RespSources.class);
		return resp;
	}

	private static void getAllArticlesFromSource(String sourceID, int j) throws MalformedURLException, IOException, ProtocolException {
		loadAPIKey();
		
		System.out.println("\n\n--------------------- Source "+j+": "+sourceID+" -------------------------");
		URL obj = new URL("https://newsapi.org/v1/articles?source="+sourceID+"&apiKey="+YOUR_APIKEY);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		int responseCode = con.getResponseCode();
		System.out.println("GET Response Code :: " + responseCode);
		
		if (responseCode == HttpURLConnection.HTTP_OK) { 
			// success
			StringBuffer response = obtainResponse(con);			
			String prettyJsonString = formatJson(response.toString());
//			System.out.println(prettyJsonString);
			RespArticles resp = buildRespPOJO(prettyJsonString);
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

	private static RespArticles buildRespPOJO(String prettyJsonString) {
		Gson gson = new Gson();
		RespArticles resp = gson.fromJson(prettyJsonString, RespArticles.class);
		return resp;
	}

	private static String formatJson(String string) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(string);
		String prettyJsonString = gson.toJson(je);
		return prettyJsonString;
	}

	private static StringBuffer obtainResponse(HttpURLConnection con) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		return response;
	}
	
	private static Set<String> tokenize(String text) throws IOException {
		CustomAnalyzer analyzer = CustomAnalyzerFactory.buildNewsAnalyzer();
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
	
	private static void loadAPIKey() throws FileNotFoundException {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		File file = new File(classloader.getResource("newsKey.txt").getFile());
		Scanner s = new Scanner(file);
		if (s.hasNextLine())
			YOUR_APIKEY=s.nextLine();
		s.close();
	}
	
}