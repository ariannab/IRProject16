package model;
import java.util.List;

public class RespArticles {
	String status;
	String source;
	String sortBy;
	List<Article> articles;
	
	public RespArticles(String status, String source, String sortBy, List<Article> articles) {
		super();
		this.status = status;
		this.source = source;
		this.sortBy = sortBy;
		this.articles = articles;
	}

	public List<Article> getArticles() {
		return articles;
	}
	
	
	
	
	
}
