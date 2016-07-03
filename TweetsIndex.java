package tweetdatabase;

import java.io.File;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.util.Version;

public class TweetsIndex {

	public static void main(String[] args) throws Exception {
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_45);
		Directory directory = new MMapDirectory(new File("tweets_index"));
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_45,
				analyzer);
		IndexWriter iwriter = new IndexWriter(directory, config);

		Scanner scan = new Scanner(new File("clean.txt"));
		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			System.out.println(line);
			String[] elements = line.split("\\|\\|\\|\\|");
			if (elements.length != 2) {
				continue;
			}
			Document doc = new Document();
			doc.add(new Field("tweetid", elements[0], TextField.TYPE_STORED));
			doc.add(new Field("content", elements[1], TextField.TYPE_NOT_STORED));
			iwriter.addDocument(doc);
		}
		iwriter.forceMerge(2);
		iwriter.close();
	}

}
