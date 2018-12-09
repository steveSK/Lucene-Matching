package matching.lucene;

import matching.lucene.analyzers.NgramAnalyzer;
import matching.lucene.analyzers.SkipGramAnalyzerWithTokenizer;
import matching.lucene.comparators.StringComparatorType;
import matching.lucene.distances.NGramDistance;
import matching.lucene.distances.NameFrequencyDistance;
import matching.lucene.helpers.FileMatcher;
import matching.lucene.helpers.IndexerHelper;
import matching.lucene.helpers.SearchHelper;
import matching.lucene.utils.RecordToMatch;
import matching.lucene.utils.SystemConstants;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.spell.StringDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by stefan on 9/13/16.
 *
 * Matching Example with names with blocking using country
 *
 */
public class LuceneMain {


    private static Logger logger = LoggerFactory.getLogger(LuceneMain.class);

    private static final String BLOCKING_FIELD = "countries";
    private static final String DATA_DIR = "/home/stefan/matching/data/test.dataset";
    private static final String NAMES_TO_MATCH = "/home/stefan/matching/data/names";
    private static final String NAME_FREQUENCY_LIST = "/home/stefan/matching-data/name-frequencies";
    private static final String SPELL_CHECKER_SOURCE_FIELD_NAME = "name";
    private static final List<String> fieldsToCheck = new ArrayList<>();
    private static final double minimalMatchRatio = 0.75;

    private static Analyzer analyzerToTest =  new NgramAnalyzer(2,2);
    private static Analyzer shortAnalyzer =  new NgramAnalyzer(2,2);
    private static StringDistance stringDistance = new NGramDistance(analyzerToTest, shortAnalyzer);



    /***
     *   Main method depends on the data you dealing with, feel free to modify and import your datasets,
     *   I written here few test methods to create index, search, search with spellchecker and match against the file
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

    public static void querySearch(String name, String nameField) throws IOException {
        SearchHelper searcher = new SearchHelper(SystemConstants.INDEX_DIR, analyzerToTest, minimalMatchRatio);
        TopDocs docs = searcher.search(name,nameField);
        for (ScoreDoc scoreDoc : docs.scoreDocs) {
            Document doc = searcher.getDocument(scoreDoc);
            System.out.println(doc.getField(nameField).stringValue() + " score: " + scoreDoc.score);
        }
    }

    public static void searchWithSpellChecker(String name,String nameField, String blockingCriteria) throws IOException {
        SearchHelper searcher = new SearchHelper(SystemConstants.INDEX_DIR, analyzerToTest, minimalMatchRatio,stringDistance,fieldsToCheck, StringComparatorType.BLOCKING,BLOCKING_FIELD);

        TopDocs docs = searcher.searchWithSpellchecker(new RecordToMatch(name,blockingCriteria));
        for (ScoreDoc scoreDoc : docs.scoreDocs) {
            Document doc = searcher.getDocument(scoreDoc);
            System.out.println(doc.getField(nameField).stringValue() + " score: " + scoreDoc.score);
        }
    }


    public void testMatchAgainstFiles() throws IOException {
        SearchHelper searcher = new SearchHelper(SystemConstants.INDEX_DIR, analyzerToTest, minimalMatchRatio,stringDistance,fieldsToCheck, StringComparatorType.BLOCKING,BLOCKING_FIELD);
        FileMatcher fileMatcher = new FileMatcher(searcher);


        Map<String, TopDocs> results = fileMatcher.matchAgainstFile(NAMES_TO_MATCH, SystemConstants.CSV_PARSER_DELIMETER);
        PrintWriter writer = new PrintWriter(NAMES_TO_MATCH, "UTF-8");
        int i = 0;
        for (Map.Entry<String, TopDocs> result : results.entrySet()) {
            TopDocs topDocs = result.getValue();
            logger.info("Result n: " + (i + 1));
            writer.println("Result n: " + (i + 1));
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document doc = searcher.getDocument(scoreDoc);
                logger.info("Matched: " + result.getKey());
                writer.println("Matched: " + result.getKey());
                for (IndexableField field : doc.getFields()) {
                    logger.info("SCORE: " + scoreDoc.score);
                    writer.println("SCORE: " + scoreDoc.score);
                    logger.info(field.name() + ": " + field.stringValue());
                    writer.print(field.name() + ": " + field.stringValue());
                }
                logger.info("\n");
                writer.println("\n");
            }
            i++;
        }
        writer.flush();
        writer.close();

    }
}
