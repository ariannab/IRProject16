package model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class User {
	String name;
	Path user_index_path;
	int totFriends;
	List<RankingArticle> rankingArticles;
	UserStats ustats;
	UserStats fstats;
	
	public User(String name) {
		super();
		this.name = name;
		this.rankingArticles = new ArrayList<RankingArticle>();
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

	public int getTotFriends() {
		return totFriends;
	}

	public void setTotFriends(int totFriends) {
		this.totFriends = totFriends;
	}

}
