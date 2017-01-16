package model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class User {
	String name;
	Path user_index_path;
	List<String> timelineUser;
	List<String> timelineFriends;
	List<RankingArticle> rankingArticles;
	UserStats ustats;
	UserStats fstats;
	
	public User(String name) {
		super();
		this.name = name;
		this.timelineFriends = new ArrayList<String>();
		this.rankingArticles = new ArrayList<RankingArticle>();
	}

	public List<String> getTimelineFriends() {
		return timelineFriends;
	}
	public void setTimelineFriends(List<String> timelineFriends) {
		this.timelineFriends = timelineFriends;
	}
	public List<String> getTimelineUser() {
		return timelineUser;
	}
	public void setTimelineUser(List<String> timelineUser) {
		this.timelineUser = timelineUser;
	}

	public List<RankingArticle> getRankingArticle() {
		return rankingArticles;
	}
	public void setRankingArticle(List<RankingArticle> rankingArticles) {
		this.rankingArticles = rankingArticles;
	}

	public String getName() {
		return name;
	}
		
	public Path getUser_index_path() {
		return user_index_path;
	}
	public void setPath(Path user_index_path) {
		this.user_index_path = user_index_path;
	}
	
	
	public UserStats getUstats() {
		return ustats;
	}
	public void setUstats(UserStats ustats) {
		this.ustats = ustats;
	}
	public UserStats getFstats() {
		return fstats;
	}
	public void setFstats(UserStats fstats) {
		this.fstats = fstats;
	}

}
