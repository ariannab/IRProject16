package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


import twitter4j.IDs;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class TwitterBootUtils {
	
	/**
	 * Read Twitter4j keys from file
	 * 
	 * @return the keys
	 * @throws FileNotFoundException
	 */
	public static List<String> loadKeys() throws FileNotFoundException {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		File file = new File(classloader.getResource("res/txt/tweetKey.txt").getFile());

		List<String> list = new ArrayList<String>();
		Scanner s = new Scanner(file);
		while (s.hasNextLine()) 
			list.add(s.nextLine());
		s.close();

		return list;
	}
	
	/**
	 * Read usernames from file
	 * 
	 * @return the usernames
	 * @throws FileNotFoundException
	 */
	public static List<String> loadUsernames() throws FileNotFoundException {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		File file = new File(classloader.getResource("res/txt/usernames.txt").getFile());
		
		List<String> list = new ArrayList<String>();
		Scanner s = new Scanner(file);
		while (s.hasNextLine())
			list.add(s.nextLine());

		s.close();
		return list;
	}
	
	/**
	 * Get timeline of a certain twitter user as plain String
	 * 
	 * @param twitter
	 * @param userName
	 * @param nTweets
	 * @return the String representing the timeline
	 * @throws TwitterException
	 */
	public static String getStringTimeline(Twitter twitter, String userName, int nTweets) throws TwitterException {
		Paging page = new Paging (1, nTweets);
		
		try {
			List<Status> statuses = twitter.getUserTimeline(userName, page);
			String timeline = "";
			for (Status status : statuses) {
				timeline += status.getText() + " " + "\n\n";
			}
			return timeline;
		} catch (TwitterException e) {
			//timeline is private, ignore
		}
		return "";
	}
	
	/**
	 * Get list of "followers" IDs for a certain Twitter user
	 * 
	 * @param twitter
	 * @param username
	 * @return the followers list (IDs)
	 */
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

	/**
	 * Get list of "following" IDs for a certain Twitter user
	 * 
	 * @param twitter
	 * @param username
	 * @return the following list (IDs)
	 */
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
	
	/**
	 * Get friends' timelines as Strings stored in list
	 * 
	 * @param twitter
	 * @param friends
	 * @return the list of Strings representing all the timelines
	 * @throws TwitterException
	 */
	public static List<String> getFriendsTimeline(Twitter twitter, List<Long> friends) throws TwitterException {
		//we retrieve the default number of tweets (20) for friends
		int nTweets = 20;
		String timeline;
		List<String> friendsTimeline = new ArrayList<String>();
		for (Long friend : friends) {
			timeline = "";
			timeline = TwitterBootUtils.getStringTimeline(twitter, twitter.showUser(friend).getScreenName(), nTweets);
			if (timeline != "") {
				friendsTimeline.add(timeline);
			}
		}
		return friendsTimeline;
	}
}
