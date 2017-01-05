package analyzers;

import java.io.IOException;

import org.apache.lucene.analysis.custom.CustomAnalyzer;

public class CustomAnalyzerFactory {

	public static CustomAnalyzer buildTweetAnalyzer() throws IOException {
		String urlRegex = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
		CustomAnalyzer analyzer = CustomAnalyzer.builder()
				.addCharFilter("patternreplace", "pattern",
						urlRegex, "replacement", "")
				.withTokenizer("lowercase").addTokenFilter("length", "min", "3", "max", "100")
				.addTokenFilter("stop", "words",
						"mysql_stopwords.txt", "format",
						"wordset")
				.addTokenFilter("porterstem")
				.build();
		return analyzer;
	}

	public static CustomAnalyzer buildNewsAnalyzer() throws IOException {
		CustomAnalyzer analyzer = CustomAnalyzer.builder()
				.withTokenizer("lowercase").addTokenFilter("length", "min", "3", "max", "100")
				.addTokenFilter("stop", "words",
						"mysql_stopwords.txt", "format",
						"wordset")
				.addTokenFilter("porterstem")
				.build();
		return analyzer;
	}
}
