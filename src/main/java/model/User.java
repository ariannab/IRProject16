package model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class User {
	String name;
	Path user_index_path;
	String timelineUser;
	List<String> timelineFriends;
	List<String> rankingArticle;
	
	
	public List<String> getTimelineFriends() {
		return timelineFriends;
	}
	public void setTimelineFriends(List<String> timelineFriends) {
		this.timelineFriends = timelineFriends;
	}
	public String getTimelineUser() {
		return timelineUser;
	}
	public void setTimelineUser(String timelineUser) {
		this.timelineUser = timelineUser;
	}
	public User(String name) {
		super();
		this.name = name;
		this.tweets=new HashSet<String>();
		this.fTweets=new HashSet<String>();
		this.timelineFriends = new ArrayList<String>();
		this.rankingArticle = new ArrayList<String>();
	}
	public List<String> getRankingArticle() {
		return rankingArticle;
	}
	public void setRankingArticle(List<String> rankingArticle) {
		this.rankingArticle = rankingArticle;
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
	
	public void adjustProfile(){
		//it is useless to retain duplicated tags,
		//we eliminate the intersection by giving priority
		//to User's own tweets
		this.fTweets.removeAll(tweets);
	}
	public Path getUser_index_path() {
		return user_index_path;
	}
	public void setPath(Path user_index_path) {
		this.user_index_path = user_index_path;
	}

}
