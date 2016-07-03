package tweetdatabase;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

/**
 * Created by ed on 1/22/15.
 */
public class LuceneTest {

	public static void main(String args[]) throws Exception {

		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_45);
		Directory directory = new RAMDirectory();
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_45,
				analyzer);
		// LMDirichletSimilarity similarity = new LMDirichletSimilarity(2000);
		LMJelinekMercerSimilarity similarity = new LMJelinekMercerSimilarity(
				0.01f);
		config.setSimilarity(similarity);
		IndexWriter indexWriter = new IndexWriter(directory, config);

		Document doc = new Document();
		TextField textField = new TextField("content", "", Field.Store.YES);

		String[] contents = { "Humpty Dumpty sat on a wall, something"};
		for (String content : contents) {
			textField.setStringValue(content);
			doc.removeField("content");
			doc.add(textField);
			indexWriter.addDocument(doc);
		}

		indexWriter.commit();

		IndexReader indexReader = DirectoryReader.open(directory);
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		indexSearcher.setSimilarity(similarity);
		QueryParser queryParser = new QueryParser(Version.LUCENE_45,"content", analyzer);
		Query query = queryParser.parse("humpty dumpty");

		TopDocs topDocs = indexSearcher.search(query, 100);
		for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
			doc = indexReader.document(scoreDoc.doc);
		
			System.out.println(scoreDoc.score + ": "
					+ doc.getField("content").stringValue());
		}
	}
}
