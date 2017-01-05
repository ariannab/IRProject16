package analyzers;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import utils.bootUtils;

public class Indexing {

	private static String YOUR_KEY;
	private static String YOUR_SECRET;
	private static String YOUR_TOKEN;
	private static String YOUR_TOKENSECRET;

	public static void userIndexing(String username, String timeline, List<String> friendsTimeline) throws IOException {
		Directory dir = FSDirectory.open(new File("./user_index").toPath());
		CustomAnalyzer analyzer = CustomAnalyzerFactory.buildTweetAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		Similarity similarity = new BM25Similarity(); // Indexing with BM25
		config.setSimilarity(similarity);
		config.setOpenMode(OpenMode.CREATE);
		IndexWriter iwriter = new IndexWriter(dir, config);

		Field userNameField = new StringField("username", username, Field.Store.YES);

		Document profile = new Document();
		profile.add(userNameField);
		profile.add(new TextField("tags", timeline, Field.Store.YES));
		profile.add(new TextField("ftags", friendsTimeline.toString(), Field.Store.YES));

		iwriter.addDocument(profile);

		iwriter.close();

	}
	
	public static void main(String args[]) throws TwitterException, IOException{
		List<String> list = bootUtils.loadKeys();
		YOUR_KEY = list.get(0);
		YOUR_SECRET = list.get(1);
		YOUR_TOKEN = list.get(2);
		YOUR_TOKENSECRET = list.get(3);

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(YOUR_KEY).setOAuthConsumerSecret(YOUR_SECRET)
				.setOAuthAccessToken(YOUR_TOKEN).setOAuthAccessTokenSecret(YOUR_TOKENSECRET);

		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();

		String userName = bootUtils.loadUsernames();
		String timeline = bootUtils.getStringTimeline(twitter, userName);
		
		List<Long> friends = bootUtils.getFollowingList(twitter, userName);
		friends.retainAll(bootUtils.getFollowersList(twitter, userName));
		List<String> friendsTimeline = bootUtils.getFriendsTimeline(twitter, friends);
		System.out.println("Now building user index...");
		userIndexing(userName, timeline, friendsTimeline);
		
	}
}
