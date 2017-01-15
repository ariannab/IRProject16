package model;

public class Article {
	String author;
	String title;
	String description;
	String url;
	String urlToImage;
	String publishedAt;
	
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Article other = (Article) obj;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}
	
	
	
}
