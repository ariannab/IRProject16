package model;

public class RankingArticle {
	String title;
	String source;
	float score;
	
	public String getTitle() {
		return title;
	}

	public String getSource() {
		return source;
	}

	public float getScore() {
		return score;
	}

	public RankingArticle(String title, String source, float score) {
		super();
		this.title = title;
		this.source = source;
		this.score = score;
	}
	
}
