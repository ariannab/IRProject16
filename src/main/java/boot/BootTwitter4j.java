package boot;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import analyzers.CustomAnalyzerFactory;
import model.User;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import utils.TwitterBootUtils;

public class BootTwitter4j {

	static String YOUR_KEY;
	static String YOUR_SECRET;
	static String YOUR_TOKEN;
	static String YOUR_TOKENSECRET;
	static String userName;

	public static void main(String[] args) throws TwitterException, InterruptedException, IOException {
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

		userName = TwitterBootUtils.loadUsernames();
		User user1 = new User(userName);

		// System.out.println("Friends of " + userName + " are in total " +
		// friends.size());
		// System.out.println("\nAnd his timeline is...\n");
		// printTimeline(twitter, userName);

		String timeline = TwitterBootUtils.getStringTimeline(twitter, userName);

		CustomAnalyzer analyzer = CustomAnalyzerFactory.buildTweetAnalyzer();
		try {
			user1.getTweets().addAll(tokenize(timeline, analyzer));
		} catch (IOException e) {
			e.printStackTrace();
		}

		List<Long> friends = TwitterBootUtils.getFollowingList(twitter, userName);
		friends.retainAll(TwitterBootUtils.getFollowersList(twitter, userName));

		System.out.println("User " + userName + " has " + friends.size() + " friends in total.");
		System.out.println("\n...Now retrieving tweets, please wait...");
		List<String> friendsTimeline = TwitterBootUtils.getFriendsTimeline(twitter, friends);
		int i = 0;
		for (String ft : friendsTimeline) {
			Set<String> fTweets = tokenize(ft, analyzer);
			// for (String t : fTweets) {
			// System.out.println(t);
			// }
			user1.getfTweets().addAll(fTweets);
		}
		user1.adjustProfile();

		i = 0;
		System.out.println("\n----------------------------------- USER'S TWEETS -------------------------------------");
		for (String t : user1.getTweets()) {
			i++;
			System.out.println("#" + i + " " + t);
		}

		i = 0;
		System.out.println("\n\n--------------------------------- FRIENDS' TWEETS ------------------------------");
		for (String t : user1.getfTweets()) {
			i++;
			System.out.println("#" + i + " " + t);
		}
		analyzer.close();

	}


	private static Set<String> tokenize(String text, Analyzer analyzer) throws IOException {
		TokenStream stream = analyzer.tokenStream("field", text);
		CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);
		Set<String> tweets = new HashSet<String>();
		try {
			stream.reset();
			while (stream.incrementToken()) {
				// System.out.println(termAtt.toString());
				tweets.add(termAtt.toString());
			}
			stream.end();
			return tweets;
		} finally {
			stream.close();
		}
	}
}
