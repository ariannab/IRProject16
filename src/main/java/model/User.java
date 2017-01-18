package model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class User {
	String name;
	Path userIndex;
	int totFriends;
	List<RankingArticle> rankingArticles;
	TopTermsStats ustats;
	TopTermsStats fstats;
	
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
		return userIndex;
	}
	public void setPath(Path user_index_path) {
		this.userIndex = user_index_path;
	}	
	
	public TopTermsStats getUstats() {
		return ustats;
	}
	public void setUstats(TopTermsStats ustats) {
		this.ustats = ustats;
	}
	public TopTermsStats getFstats() {
		return fstats;
	}
	public void setFstats(TopTermsStats fstats) {
		this.fstats = fstats;
	}

	public int getTotFriends() {
		return totFriends;
	}

	public void setTotFriends(int totFriends) {
		this.totFriends = totFriends;
	}

}
