package analyzers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StoredField;
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
import model.User;
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

	/**
	 * Build user document (i.e. the profile)
	 * 
	 * @param username
	 * @param timeline
	 * @param friendsTimeline
	 * @param friends
	 * @return the document
	 * @throws IOException
	 */
	public static Document userDoc(String username, String timeline, List<String> friendsTimeline, int friends) throws IOException {
		Field userNameField = new StringField("username", username, Field.Store.YES);
		StoredField  totFriendsField = new StoredField ("friends", friends);
		
		Document profile = new Document();
		profile.add(userNameField);
		profile.add(totFriendsField);
		
		FieldType myFieldType = new FieldType(TextField.TYPE_STORED);
		//we need to store the term vectors if we want the term frequency in the user document
		myFieldType.setStoreTermVectors(true);		
		Field utags = new Field("utags", timeline, myFieldType);		
//		TextField utags = new TextField("utags", timeline, Field.Store.YES);
		profile.add(utags);
		
//		TextField ftags = new TextField("ftags", friendsTimeline.toString(), Field.Store.YES);		
		Field ftags = new Field("ftags", friendsTimeline.toString(), myFieldType);
		profile.add(ftags);
		
		return profile;

	}
	
	
	/**
	 * Build article document 
	 * 
	 * @param title
	 * @param description
	 * @param sourceID
	 * @return the document
	 * @throws IOException
	 */
	public static Document articleDoc(String title, String description, String sourceID) throws IOException{		
		Field titleField = new StringField("title", title, Field.Store.YES);	
		Field sourceField = new StringField("source", sourceID, Field.Store.YES);
		Document article = new Document();
		article.add(titleField);
		article.add(sourceField);
		article.add(new TextField("atags", title+" "+description, Field.Store.YES));
		
		return article;
		
	}
	

	public static void main() throws TwitterException, IOException{
		System.out.println("\nBuilding news index...");
		Path artIndex = buildNewsIndex();
//		Path artIndex = Paths.get("./indexes/article_index");
		
		System.out.println("\nBuilding user index...");
		//Path userIndex = buildUserIndex();	
		Path userIndex = Paths.get("./indexes/profile_index");
		
		System.out.println("\n\nNow querying!");

		CustomAnalyzer analyzer = CustomAnalyzerFactory.buildTweetAnalyzer();
		Querying.makeQuery(userIndex, artIndex);		
		analyzer.close();
	}
	
	/**
	 * Build the complete user index
	 * 
	 * @return the path where the index is stored
	 * @throws IOException
	 * @throws TwitterException
	 */
	public static User buildUserIndex(String txtUser) throws IOException, TwitterException {
		User user = new User(txtUser);
		Path userIndex = new File("./indexes/profile_index").toPath();
		Directory dir = FSDirectory.open(userIndex);
		user.setPath(userIndex);
		

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
		String userName = txtUser;
		System.out.println("User is: "+userName);
		String timeline = TwitterBootUtils.getStringTimeline(twitter, userName, 100);
		user.setTimelineUser(timeline);
		
		List<Long> friends = TwitterBootUtils.getFollowingList(twitter, userName);
		friends.retainAll(TwitterBootUtils.getFollowersList(twitter, userName));
		
		int totalFriends = friends.size();
		System.out.println("\nUser is: "+userName+" and has "+totalFriends+" friends");
		List<String> friendsTimeline = TwitterBootUtils.getFriendsTimeline(twitter, friends);
		user.setTimelineFriends(friendsTimeline);


		Document profile = userDoc(userName, timeline, friendsTimeline, totalFriends);
		iwriter2.addDocument(profile);
		iwriter2.close();
		analyzer.close();
		
		return user;
		
	}

	/**
	 * Build the complete news index
	 * 
	 * @return the path where the index is stored
	 * @throws IOException
	 */
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
			articles = NewsBootUtils.getAllArticlesFromSource(id, i);
			for(Article a : articles){
				Document article = articleDoc(a.getTitle(), a.getDescription(), id);	
				iwriter1.addDocument(article);
			}
		}
		iwriter1.close();
		analyzer.close();
		
		return artIndex;
		
	}
	
	public static void main(String args[]) throws TwitterException, IOException{		
		System.out.println("\nBuilding news index, then user index...");
		Path artIndex = Paths.get("./indexes/article_index");
		Path userIndex = Paths.get("./indexes/profile_index");
		
//		Path artIndex = buildNewsIndex();
//		Path userIndex = buildUserIndex();	
		
		System.out.println("\n\nNow querying!");

		Querying.makeQuery(userIndex, artIndex);	
	}

}
