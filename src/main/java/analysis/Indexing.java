package analysis;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.misc.HighFreqTerms;
import org.apache.lucene.misc.TermStats;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import model.Article;
import model.User;
import model.UserStats;
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
		profile.add(utags);
				
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
		
	/**
	 * Build the complete user index
	 * 
	 * @return the path where the index is stored
	 * @throws Exception 
	 */
	public static User buildUserIndex(String userName) throws Exception {
		User user = new User(userName);
		Path userIndex = new File("./indexes/profiles/"+userName).toPath();
		Directory dir = FSDirectory.open(userIndex);		

		CustomAnalyzer analyzer = CustomAnalyzerFactory.buildTweetAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		Similarity similarity = new BM25Similarity(); // Indexing with BM25
		config.setSimilarity(similarity);
		config.setOpenMode(OpenMode.CREATE);
		
		IndexWriter iwriter = new IndexWriter(dir, config);
		
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
		String timeline = TwitterBootUtils.getStringTimeline(twitter, userName, 100);
		
		List<Long> friends = TwitterBootUtils.getFollowingList(twitter, userName);
		friends.retainAll(TwitterBootUtils.getFollowersList(twitter, userName));
		
		int totalFriends = friends.size();
		System.out.println("\nUser is: "+userName+" and has "+totalFriends+" friends");
		List<String> friendsTimeline = TwitterBootUtils.getFriendsTimeline(twitter, friends);

		Document profile = userDoc(userName, timeline, friendsTimeline, totalFriends);
		iwriter.addDocument(profile);
		iwriter.close();
		analyzer.close();

		user.setPath(userIndex);

//		List<String> tagsTimeline = getHighestTerms(userIndex, "utags", 100);
//		List<String> fTagsTimeline = getHighestTerms(userIndex, "ftags", 100);		
//		user.setTimelineUser(tagsTimeline);
//		user.setTimelineFriends(fTagsTimeline);
		
		user.setUstats(getHighestTerms(userIndex, "utags", 100));
		user.setFstats(getHighestTerms(userIndex, "ftags", 100));
		return user;
		
	}

	/**
	 * Build the complete news index
	 * 
	 * @return the path where the index is stored
	 * @throws IOException
	 */
	public static Path buildNewsIndex() throws IOException {
		Path artIndex = new File("./indexes/article_index").toPath();
		Directory dir = FSDirectory.open(artIndex);
		CustomAnalyzer analyzer = CustomAnalyzerFactory.buildTweetAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		Similarity similarity = new BM25Similarity(); // Indexing with BM25
		config.setSimilarity(similarity);
		config.setOpenMode(OpenMode.CREATE);
		
		IndexWriter iwriter = new IndexWriter(dir, config);
		
		//retrieving and indexing one article
		Set<String> enSourcesIDs = NewsBootUtils.getSourcesIDs();
		int i = 0;
		Set<Article> articles = null;
		for(String id : enSourcesIDs){
			i++;
			articles = NewsBootUtils.getAllArticlesFromSource(id, i);
			if (articles != null) {
				for (Article a : articles) {
					Document article = articleDoc(a.getTitle(), a.getDescription(), id);
					iwriter.addDocument(article);
				}
			}
		}
		iwriter.close();
		analyzer.close();
		
		return artIndex;
		
	}
	
	/**
	 * Get most "total" frequent terms, 
	 * of user's timeline or his friends'
	 * 
	 * @param userIndex
	 * @param field (can be utags for users tags, ftags for friends tags)
	 * @return a UserStats object i.e. string of terms and their freq
	 * @throws Exception
	 */
	public static UserStats getHighestTerms(Path userIndex, String field, int total) throws Exception{
		Directory userDir = FSDirectory.open(userIndex);

		DirectoryReader uReader = DirectoryReader.open(userDir);
		TermStats[] terms = HighFreqTerms.getHighFreqTerms(uReader, 
				total, field, new HighFreqTerms.TotalTermFreqComparator());
		
		UserStats resultList = new UserStats();
		
		 for (int i = 0; i < terms.length; i++) {
			  resultList.getTerms().add(terms[i].termtext.utf8ToString());
			  resultList.getFreq().add((int) terms[i].totalTermFreq);
		 }
		 
		 return resultList;
	}
	
	/**
	 * Get the highest frequency (referred to field). 
	 * Utility for normalization of frequencies
	 * 
	 * @param userIndex
	 * @param field
	 * @return the highest frequency for terms in field
	 * @throws Exception
	 */
	public static int getHighestFreq(Path userIndex, String field) throws Exception{
		Directory userDir = FSDirectory.open(userIndex);

		DirectoryReader uReader = DirectoryReader.open(userDir);
		TermStats[] terms = HighFreqTerms.getHighFreqTerms(uReader, 
				1, field, new HighFreqTerms.TotalTermFreqComparator());
		
		 return (int) terms[0].totalTermFreq;
	}
	
	public static void main(String args[]) throws Exception{		
		System.out.println("\nBuilding news index, then user index...");
		Path artIndex = Paths.get("./indexes/article_index");
		Path userIndex = Paths.get("./indexes/profile_index");
		
		System.out.println("\n\nNow querying!");

		Querying.makeQuery(userIndex, artIndex);
	}

	/**
	 * Read an already locally stored profile.
	 * 
	 * @param userName
	 * @return the User object holding the retrieved information
	 * @throws Exception
	 */
	public static User readUserIndex(String userName) throws Exception {
		Path userIndex = new File("./indexes/profiles/"+userName).toPath();
//		List<String> timeline = getHighestTerms(userIndex, "utags", 100);
//		List<String> friendsTimeline = getHighestTerms(userIndex, "ftags", 100);
//		
		User user = new User(userName);
		user.setPath(userIndex);

		user.setUstats(getHighestTerms(userIndex, "utags", 100));
		user.setFstats(getHighestTerms(userIndex, "ftags", 100));
//		user.setTimelineUser(timeline);
//		user.setTimelineFriends(friendsTimeline);
		return user;
	}

}
