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
	public static List<String> loadKeys() throws FileNotFoundException {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		File file = new File(classloader.getResource("tweetKey.txt").getFile());

		List<String> list = new ArrayList<String>();
		Scanner s = new Scanner(file);
		while (s.hasNextLine()) 
			list.add(s.nextLine());
		s.close();

		return list;
	}
	
	public static String loadUsernames() throws FileNotFoundException {
		// TODO this method will retrieve 10 usernames from the file,
		// for now it is just one (the first)
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		File file = new File(classloader.getResource("usernames.txt").getFile());

		List<String> list = new ArrayList<String>();
		Scanner s = new Scanner(file);
		if (s.hasNextLine())
			list.add(s.nextLine());

		s.close();
		return list.get(0);
	}
	
	public static String getStringTimeline(Twitter twitter, String userName) throws TwitterException {
		try {
			List<Status> statuses = twitter.getUserTimeline(userName, new Paging());
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
	
	
	public static void printTimeline(Twitter twitter, String userName) throws TwitterException {
		// First param of Paging() is the page number, second is the number per
		// page (this is capped around 200 I think)
		Paging paging = new Paging(1, 100);
		List<Status> statuses = twitter.getUserTimeline(userName, paging);
		for (Status status : statuses) {
			System.out.println(status.getUser().getName() + ":" + status.getText());
		}
	}
	

	public static List<String> getFriendsTimeline(Twitter twitter, List<Long> friends) throws TwitterException {
		String timeline;
		List<String> friendsTimeline = new ArrayList<String>();
		for (Long friend : friends) {
			timeline = "";
			timeline = TwitterBootUtils.getStringTimeline(twitter, twitter.showUser(friend).getScreenName());
			if (timeline != "") {
				friendsTimeline.add(timeline);
			}
		}
		return friendsTimeline;
	}
}
