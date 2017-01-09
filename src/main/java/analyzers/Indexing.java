package analyzers;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import model.Article;
import model.RespArticles;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import utils.NewsBootUtils;
import utils.TwitterBootUtils;

public class Indexing {

	private static String YOUR_KEY;
	private static String YOUR_SECRET;
	private static String YOUR_TOKEN;
	private static String YOUR_TOKENSECRET;

	public static Document userDoc(String username, String timeline, List<String> friendsTimeline) throws IOException {
		Field userNameField = new StringField("username", username, Field.Store.YES);
		
		Document profile = new Document();
		profile.add(userNameField);
		
		FieldType myFieldType = new FieldType(TextField.TYPE_STORED);
		myFieldType.setStoreTermVectors(true);		
		Field utags = new Field("utags", timeline, myFieldType);		
//		TextField utags = new TextField("utags", timeline, Field.Store.YES);
		profile.add(utags);
		
//		TextField ftags = new TextField("ftags", friendsTimeline.toString(), Field.Store.YES);		
		Field ftags = new Field("ftags", friendsTimeline.toString(), myFieldType);
		profile.add(ftags);
		
		return profile;

	}
	
	public static Document articleDoc(String title, String description, String sourceID) throws IOException{		
		Field titleField = new StringField("title", title, Field.Store.YES);	
		Field sourceField = new StringField("source", sourceID, Field.Store.YES);
		Document article = new Document();
		article.add(titleField);
		article.add(sourceField);
		article.add(new TextField("atags", title+" "+description, Field.Store.YES));
		
		return article;
		
	}
	
	public static void main(String args[]) throws TwitterException, IOException{		
		System.out.println("\nBuilding news index...");
		Path artIndex = buildNewsIndex();
//		Path artIndex = Paths.get("./indexes/article_index");
		
		System.out.println("\nBuilding user index...");
		Path userIndex = buildUserIndex();	
//		Path userIndex = Paths.get("./indexes/profile_index");
		
		System.out.println("\n\nNow querying!");

		CustomAnalyzer analyzer = CustomAnalyzerFactory.buildTweetAnalyzer();
		Querying.makeQuery(userIndex, artIndex, analyzer);		
		analyzer.close();
	}
	
	private static Path buildUserIndex() throws IOException, TwitterException {
		Path userIndex = new File("./indexes/profile_index").toPath();
		Directory dir = FSDirectory.open(userIndex);

		CustomAnalyzer analyzer = CustomAnalyzerFactory.buildTweetAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		Similarity similarity = new BM25Similarity(); // Indexing with BM25
		config.setSimilarity(similarity);
		config.setOpenMode(OpenMode.CREATE);
		
		IndexWriter iwriter2 = new IndexWriter(dir, config);
		
		//retrieving and indexing one user
		List<String> list = TwitterBootUtils.loadKeys();
		YOUR_KEY = list.get(0);
		YOUR_SECRET = list.get(1);
		YOUR_TOKEN = list.get(2);
		YOUR_TOKENSECRET = list.get(3);

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(YOUR_KEY).setOAuthConsumerSecret(YOUR_SECRET)
				.setOAuthAccessToken(YOUR_TOKEN).setOAuthAccessTokenSecret(YOUR_TOKENSECRET);

		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		String userName = TwitterBootUtils.loadUsernames();
		System.out.println("User is: "+userName);
		String timeline = TwitterBootUtils.getStringTimeline(twitter, userName);
		
		List<Long> friends = TwitterBootUtils.getFollowingList(twitter, userName);
		friends.retainAll(TwitterBootUtils.getFollowersList(twitter, userName));
		List<String> friendsTimeline = TwitterBootUtils.getFriendsTimeline(twitter, friends);


		Document profile = userDoc(userName, timeline, friendsTimeline);
		iwriter2.addDocument(profile);
		iwriter2.close();
		analyzer.close();
		
		return userIndex;
		
	}

	private static Path buildNewsIndex() throws IOException {
		Path artIndex = new File("./indexes/article_index").toPath();
		Directory dir = FSDirectory.open(artIndex);
		CustomAnalyzer analyzer = CustomAnalyzerFactory.buildTweetAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		Similarity similarity = new BM25Similarity(); // Indexing with BM25
		config.setSimilarity(similarity);
		config.setOpenMode(OpenMode.CREATE);
		
		IndexWriter iwriter1 = new IndexWriter(dir, config);
		
		//retrieving and indexing one article
		Set<String> enSourcesIDs = NewsBootUtils.getSourcesIDs();
		int i = 0;
		Set<Article> articles = null;
		for(String id : enSourcesIDs){
			i++;
			articles = getAllArticlesFromSource(id, i);
			for(Article a : articles){
				Document article = articleDoc(a.getTitle(), a.getDescription(), id);	
				iwriter1.addDocument(article);
			}
		}
		iwriter1.close();
		analyzer.close();
		
		return artIndex;
		
	}


	private static Set<Article> getAllArticlesFromSource(String sourceID, int j) throws MalformedURLException, IOException, ProtocolException {
		String YOUR_APIKEY = NewsBootUtils.loadAPIKey();
		
//		System.out.println("\n\n--------------------- Source "+j+": "+sourceID+" -------------------------");
		URL obj = new URL("https://newsapi.org/v1/articles?source="+sourceID+"&apiKey="+YOUR_APIKEY);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		int responseCode = con.getResponseCode();
//		System.out.println("GET Response Code :: " + responseCode);
		
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
