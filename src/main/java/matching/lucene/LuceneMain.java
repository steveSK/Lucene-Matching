package matching.lucene;

import matching.lucene.analyzers.SkipGramAnalyzerWithTokenizer;
import matching.lucene.distances.NameFrequencyDistance;
import matching.lucene.helpers.IndexerHelper;
import matching.lucene.helpers.SearchHelper;
import matching.lucene.utils.SystemConstants;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by stefan on 9/13/16.
 */
public class LuceneMain {


    private static Logger logger = LoggerFactory.getLogger(LuceneMain.class);

    private static final String BLOCKING_FIELD = "countries";
    private static final String DATA_DIR = "/home/stefan/matching/data/test.dataset";
    private static final String NAME_FREQUENCY_LIST = "/home/stefan/matching-data/name-frequencies";
    private static final String SPELL_CHECKER_SOURCE_FIELD_NAME = "name";
    private static final List<String> fieldsToCheck = new ArrayList<>();
    private static final double minimalMatchRatio = 0.75;

    public static Analyzer analyzerToTest = new SkipGramAnalyzerWithTokenizer(1, 3);






    /***
     *   main method depends on the data you dealing with, feel free to modify and import your datasets
     */
    public static void main(String[] args) {
        NameFrequencyDistance distance = null;
        try {
            distance = new NameFrequencyDistance(NAME_FREQUENCY_LIST);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(),e);
        }
        System.out.println(distance.getDistance("stefan repcek", "stefan repcik"));


    }

    public static void createIndex() throws IOException {
        IndexerHelper indexer = new IndexerHelper(SystemConstants.INDEX_DIR, SystemConstants.INDEX_SPELL_CHECKER_DIR, analyzerToTest);
        indexer.clearIndex();
        indexer.indexSimpleFile(new File(DATA_DIR));
    }

    public static void testSearch(String nameField, String name, String blockingKey) throws IOException, ParseException {
        SearchHelper searcher = new SearchHelper(SystemConstants.INDEX_DIR, fieldsToCheck, analyzerToTest, minimalMatchRatio, false, BLOCKING_FIELD);
        TopDocs docs = searcher.searchWithSpellchecker(name,blockingKey);
        for (ScoreDoc scoreDoc : docs.scoreDocs) {
            Document doc = searcher.getDocument(scoreDoc);
            System.out.println(doc.getField(nameField).stringValue() + " score: " + scoreDoc.score);
        }
    }

   /* public void createMultipleSearch() throws IOException, ParseException {
        Map<String, TopDocs> results = searcher.matchAgainstFile(wordsTomatchFile, fieldsToCheck);
        PrintWriter writer = new PrintWriter(fileToWriteResults, "UTF-8");
        int i = 0;
        for (Map.Entry<String, TopDocs> result : results.entrySet()) {
            TopDocs topDocs = result.getValue();
            System.out.println("Result n: " + (i + 1));
            writer.println("Result n: " + (i + 1));
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document doc = searcher.getDocument(scoreDoc);
                System.out.println("Matched: " + result.getKey());
                writer.println("Matched: " + result.getKey());
                for (IndexableField field : doc.getFields()) {
                    System.out.println("SCORE: " + scoreDoc.score);
                    writer.println("SCORE: " + scoreDoc.score);
                    System.out.print(field.name() + ": " + field.stringValue());
                    writer.print(field.name() + ": " + field.stringValue());
                }
                System.out.println("\n");
                writer.println("\n");
            }
            i++;
        }
        writer.flush();
        writer.close();

    } */
}
