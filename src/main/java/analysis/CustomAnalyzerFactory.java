package analysis;

import java.io.IOException;

import org.apache.lucene.analysis.custom.CustomAnalyzer;

public class CustomAnalyzerFactory {

	/**
	 * This custom analyzer eliminates URLs via pattern replace,
	 * uses a lowercase tokenizer, removes stopwords listed in the .txt,
	 * then adds some other filters (stemming, length)
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

}
