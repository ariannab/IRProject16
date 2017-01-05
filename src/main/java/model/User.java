package model;

import java.util.HashSet;
import java.util.Set;

public class User {
	String name;
	public User(String name) {
		super();
		this.name = name;
		this.tweets=new HashSet<String>();
		this.fTweets=new HashSet<String>();
	}
	Set<String> tweets;
	Set<String> fTweets;
	public String getName() {
		return name;
	}
	public Set<String> getTweets() {
		return tweets;
	}
	public Set<String> getfTweets() {
		return fTweets;
	}
	
	
}
