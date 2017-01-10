package analyzers;

import java.io.IOException;

import org.apache.lucene.analysis.custom.CustomAnalyzer;

public class CustomAnalyzerFactory {

	/**
	 * This custom analyzer for tweets eliminates URLs via pattern replace,
	 * uses a lowercase tokenizer, removes stopwords listed in the .txt,
	 * then add some other filters (as stemming and length)
	 * 
	 * @return the analyzer
	 * @throws IOException
	 */
	public static CustomAnalyzer buildTweetAnalyzer() throws IOException {
		String urlRegex = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
		CustomAnalyzer analyzer = CustomAnalyzer.builder()
				.addCharFilter("patternreplace", "pattern",
						urlRegex, "replacement", "")
				.withTokenizer("lowercase")
				.addTokenFilter("stop", "words",
						"long_stopwords.txt", "format",
						"wordset")
				.addTokenFilter("porterstem")
				.addTokenFilter("length", "min", "3", "max", "15")
				.build();
		return analyzer;
	}

//	public static CustomAnalyzer buildNewsAnalyzer() throws IOException {
//		CustomAnalyzer analyzer = CustomAnalyzer.builder()
//				.withTokenizer("lowercase").addTokenFilter("length", "min", "3", "max", "100")
//				.addTokenFilter("stop", "words",
//						"mysql_stopwords.txt", "format",
//						"wordset")
//				.addTokenFilter("porterstem")
//				.build();
//		return analyzer;
//	}
}
