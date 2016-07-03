package tweetdatabase;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

public class CSVOutput {
	public static void analyze() throws IOException {
		HashMap<String, Integer> wordtoID = new HashMap<String, Integer>();
		Scanner scan = new Scanner(new File("clean2.txt"));
		PrintWriter pw = new PrintWriter("tweets.csv");
		PrintWriter pwWordtoID = new PrintWriter("dictionary.txt");
		PrintWriter pwlineToTweetID = new PrintWriter("tweetsid.txt");
		int lineNum = 1;
		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			System.out.println(line);
			String[] elements = line.split("\\|\\|\\|\\|");
			if (elements.length != 2) {
				continue;
			}
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_45);
			TokenStream tokenStream = analyzer.tokenStream(
					LuceneConstants.CONTENTS, new StringReader(elements[1]));
			tokenStream.reset();
			CharTermAttribute term = tokenStream
					.addAttribute(CharTermAttribute.class);
			StringBuilder outputline = new StringBuilder();
			HashMap<String, Integer> ht = new HashMap<String, Integer>();

			while (tokenStream.incrementToken()) {
				String word = term.toString();
				int id;
				if (wordtoID.containsKey(word)) {
					id = wordtoID.get(word);
				} else {
					wordtoID.put(word, wordtoID.size());
					id = wordtoID.size() - 1;
				}
				if (ht.containsKey(word)) {

				} else {
					outputline.append(id + ",");
					ht.put(word, id);
				}
			}
			if (outputline.length() != 0) {
				outputline.deleteCharAt(outputline.length() - 1);
				if (outputline.length() != 0) {
					pw.println(outputline.toString());
					pwlineToTweetID.println(lineNum + " " + elements[0]);
					lineNum++;
				}
			}
		}
		Iterator<Entry<String, Integer>> iterator = wordtoID.entrySet()
				.iterator();
		while (iterator.hasNext()) {
			Entry<String, Integer> entry = iterator.next();
			pwWordtoID.println(entry.getKey() + " " + entry.getValue());
		}
		pw.flush();
		pw.close();
		pwWordtoID.flush();
		pwWordtoID.close();
		pwlineToTweetID.flush();
		pwlineToTweetID.close();
	}

	public static void main(String[] args) throws IOException {
		analyze();
	}

}

class LuceneConstants {
	public static final String CONTENTS = "contents";
	public static final String FILE_NAME = "filename";
	public static final String FILE_PATH = "filepath";
	public static final int MAX_SEARCH = 10;
}