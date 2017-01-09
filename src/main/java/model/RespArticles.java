package model;
import java.util.Set;

public class RespArticles {
	String status;
	String source;
	String sortBy;
	Set<Article> articles;
	
	public RespArticles(String status, String source, String sortBy, Set<Article> articles) {
		super();
		this.status = status;
		this.source = source;
		this.sortBy = sortBy;
		this.articles = articles;
	}

	public Set<Article> getArticles() {
		return articles;
	}
	
	
	
	
	
}
