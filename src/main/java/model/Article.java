package model;

import java.util.HashSet;
import java.util.Set;

public class Article {
	String author;
	String title;
	String description;
	String url;
	String urlToImage;
	String publishedAt;
	Set<String> tags;
	
	public Article(String author, String title, String description, String url, String urlToImage, String publishedAt) {
		super();
		this.author = author;
		this.title = title;
		this.description = description;
		this.url = url;
		this.urlToImage = urlToImage;
		this.publishedAt = publishedAt;
	}

	@Override
	public String toString() {
		return "\n\nArticle [\nauthor=" + author + ", \ntitle=" + title + ", \ndescription=" + description + ", \nurl=" + url
				+ ", \nurlToImage=" + urlToImage + ", \npublishedAt=" + publishedAt + "\n]";
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public Set<String> getTags() {
		if(tags == null)
			tags = new HashSet<String>();
		return tags;
	}
	
}