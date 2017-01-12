package utils;

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
import java.util.Scanner;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import model.Article;
import model.RespArticles;
import model.RespSources;
import model.Source;

public class NewsBootUtils {
	public static Set<String> getSourcesIDs() throws MalformedURLException, IOException, ProtocolException {
		URL obj = new URL("https://newsapi.org/v1/sources?language=en");
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		int responseCode = con.getResponseCode();
		System.out.println("GET Response Code :: " + responseCode);

		if (responseCode == HttpURLConnection.HTTP_OK) {
			StringBuffer response = obtainResponse(con);
			String prettyJsonString = formatJson(response.toString());
			Set<String> sourceIDs = new HashSet<String>();
			RespSources resp = buildSourcePOJO(prettyJsonString);
			for (Source s : resp.getSources()) {
				String id = s.getId();
				sourceIDs.add(id);
			}
			return sourceIDs;
		} else {
			System.out.println("GET request not worked");
		}
		return null;
	}

	public static StringBuffer obtainResponse(HttpURLConnection con) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		return response;
	}

	public static String formatJson(String string) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(string);
		String prettyJsonString = gson.toJson(je);
		return prettyJsonString;
	}

	public static RespArticles buildRespPOJO(String prettyJsonString) {
		Gson gson = new Gson();
		RespArticles resp = gson.fromJson(prettyJsonString, RespArticles.class);
		return resp;
	}

	private static RespSources buildSourcePOJO(String prettyJsonString) {
		Gson gson = new Gson();
		RespSources resp = gson.fromJson(prettyJsonString, RespSources.class);
		return resp;
	}

	public static String loadAPIKey() throws FileNotFoundException {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		File file = new File(classloader.getResource("newsKey.txt").getFile());
		Scanner s = new Scanner(file);
		try {
			return s.nextLine();
		} catch (Exception e) {
			return null;
		} finally {
			s.close();
		}
	}
	
	public static Set<Article> getAllArticlesFromSource(String sourceID, int j) throws MalformedURLException, IOException, ProtocolException {
		String YOUR_APIKEY = NewsBootUtils.loadAPIKey();
		
		URL obj = new URL("https://newsapi.org/v1/articles?source="+sourceID+"&apiKey="+YOUR_APIKEY);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		int responseCode = con.getResponseCode();
		
		if (responseCode == HttpURLConnection.HTTP_OK) { 
			// success
			StringBuffer response = NewsBootUtils.obtainResponse(con);			
			String prettyJsonString = NewsBootUtils.formatJson(response.toString());
			RespArticles resp = NewsBootUtils.buildRespPOJO(prettyJsonString);
			Set<Article> articles = resp.getArticles();
			
			return articles;		
		} else {
			System.out.println("GET request not worked");
		}
		return null;
	}

}
