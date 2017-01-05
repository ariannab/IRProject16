package boot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import analyzers.CustomAnalyzerFactory;
import model.User;
import twitter4j.IDs;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class BootTwitter4j {

	static String YOUR_KEY;
	static String YOUR_SECRET;
	static String YOUR_TOKEN;
	static String YOUR_TOKENSECRET;
	static String userName;

	public static void main(String[] args) throws TwitterException, InterruptedException, IOException {
		loadKeys();

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(YOUR_KEY).setOAuthConsumerSecret(YOUR_SECRET)
				.setOAuthAccessToken(YOUR_TOKEN).setOAuthAccessTokenSecret(YOUR_TOKENSECRET);

		
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();

		loadUsernames();
		User user1 = new User(userName);

		// System.out.println("Friends of " + userName + " are in total " +
		// friends.size());
		// System.out.println("\nAnd his timeline is...\n");
		// printTimeline(twitter, userName);

		String timeline = getStringTimeline(twitter, userName);

		CustomAnalyzer analyzer = CustomAnalyzerFactory.buildTweetAnalyzer();
		try {
			user1.getTweets().addAll(tokenize(timeline, analyzer));
		} catch (IOException e) {
			e.printStackTrace();
		}

		List<Long> friends = getFollowingList(twitter, userName);
		friends.retainAll(getFollowersList(twitter, userName));
		System.out.println("User "+userName+" has "+friends.size()+" friends in total.");
		System.out.println("\n...Now retrieving tweets, please wait...");
		int i = 0;
		for (Long friend : friends) {
			timeline = "";
			i++;
			// System.out.println("\n---- Friend #" + i + ": " +
			// twitter.showUser(friend).getScreenName() + " -----");
			timeline = getStringTimeline(twitter, twitter.showUser(friend).getScreenName());
			if (timeline != "") {
				try {
					Set<String> fTweets = tokenize(timeline, analyzer);
//					for (String t : fTweets) {
//						System.out.println(t);
//					}
					user1.getfTweets().addAll(fTweets);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		user1.adjustProfile();
		
		i=0;
		System.out.println("\n----------------------------------- USER'S TWEETS -------------------------------------");
		for (String t : user1.getTweets()){
			i++;
			System.out.println("#"+i+" "+t);
		}

		i=0;
		System.out.println("\n\n--------------------------------- FRIENDS' TWEETS ------------------------------");
		for (String t : user1.getfTweets()){
			i++;
			System.out.println("#"+i+" "+t);
		}
		analyzer.close();

	}

	private static void loadUsernames() throws FileNotFoundException {
		// TODO this method will retrieve 10 usernames from the file,
		// for now it is just one
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		File file = new File(classloader.getResource("usernames.txt").getFile());

		List<String> list = new ArrayList<String>();
		Scanner s = new Scanner(file);
		if (s.hasNextLine())
			list.add(s.nextLine());

		s.close();
		userName = list.get(0);
	}

	private static void loadKeys() throws FileNotFoundException {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		File file = new File(classloader.getResource("tweetKey.txt").getFile());

		List<String> list = new ArrayList<String>();
		Scanner s = new Scanner(file);
		while (s.hasNextLine()) 
			list.add(s.nextLine());
		s.close();

		YOUR_KEY = list.get(0);
		YOUR_SECRET = list.get(1);
		YOUR_TOKEN = list.get(2);
		YOUR_TOKENSECRET = list.get(3);
	}

	private static String getStringTimeline(Twitter twitter, String userName) throws TwitterException {
		Paging paging = new Paging(1, 100);
		try {
			List<Status> statuses = twitter.getUserTimeline(userName, paging);
			String timeline = "";
			for (Status status : statuses) {
				timeline += status.getText() + " ";
			}
			// System.out.println("\n\n"+timeline);
			return timeline;
		} catch (TwitterException e) {

		}
		return "";

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

	@SuppressWarnings("unused")
	private static void printTimeline(Twitter twitter, String userName) throws TwitterException {
		// First param of Paging() is the page number, second is the number per
		// page (this is capped around 200 I think)
		Paging paging = new Paging(1, 100);
		List<Status> statuses = twitter.getUserTimeline(userName, paging);
		for (Status status : statuses) {
			System.out.println(status.getUser().getName() + ":" + status.getText());
		}
	}

	public static void printFollowers(Twitter twitter, String userName) {
		try {
			IDs ids = twitter.getFollowersIDs(userName, -1);
			do {
				for (long id : ids.getIDs()) {
					/*
					 * String ID = "followers ID #" + id; String[] firstname =
					 * ID.split("#"); String first_Name = firstname[0]; String
					 * Id = firstname[1]; String Name =
					 * twitter.showUser(id).getName();
					 */
					String screenname = twitter.showUser(id).getScreenName();
					System.out.println(screenname);
				}
			} while (ids.hasNext());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<Long> getFollowersList(Twitter twitter, String username) {
		List<Long> followers = new ArrayList<Long>();
		try {
			IDs ids = twitter.getFollowersIDs(username, -1);
			do {
				for (long id : ids.getIDs())
					followers.add(id);
			} while (ids.hasNext());
			return followers;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return followers;
	}

	public static List<Long> getFollowingList(Twitter twitter, String username) {
		List<Long> following = new ArrayList<Long>();
		try {
			IDs ids = twitter.getFriendsIDs(username, -1);
			do {
				for (long id : ids.getIDs())
					following.add(id);
			} while (ids.hasNext());
			return following;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return following;
	}
}
