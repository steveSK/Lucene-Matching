package matching.lucene;

import matching.lucene.analyzers.KeyWordAnalyzerLowerCase;
import matching.lucene.analyzers.SkipGramAnalyzerWithTokenizer;
import matching.lucene.analyzers.SplitAnalyzer;
import matching.lucene.differences.ConsonantsDifference;
import matching.lucene.differences.VowelsDifference;
import matching.lucene.distances.DoubleMetaphoneDistance;
import matching.lucene.distances.LCSDistance;
import matching.lucene.distances.NameFrequencyDistance;
import matching.lucene.helpers.IndexerHelper;
import matching.lucene.helpers.SearchHelper;
import matching.lucene.schema.LuceneFieldDefinition;
import matching.lucene.schema.LuceneSchema;
import matching.lucene.utils.SystemConstants;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by stefan on 9/13/16.
 */
public class LuceneMain {


    public static Analyzer analyzerToTest = new SkipGramAnalyzerWithTokenizer(1, 3);
    private static final String BLOCK_FIELD = "countries";
    private static final String NAME_FREQUENCY_LIST = "/home/stefan/matching-data/name-frequencies";
    private static final String SPELL_CHECKER_SOURCE_FIELD_NAME = "name";
    private static final List<String> fieldsToCheck = new ArrayList<>();
    private static final double minimalMatchRatio = 0.75;

    private String dataDir;
    private String wordsTomatchFile;
    private String fileToWriteResults;
    IndexerHelper indexer;
    SearchHelper searcher;

    public LuceneMain() throws IOException {
        indexer = new IndexerHelper(SystemConstants.INDEX_DIR, SystemConstants.INDEX_SPELL_CHECKER_DIR, analyzerToTest);
        indexer.clearIndex();
        searcher = new SearchHelper(SystemConstants.INDEX_DIR, fieldsToCheck, analyzerToTest, minimalMatchRatio,false, BLOCK_FIELD);
    }
    /***
     *   main method depends on the data you dealing with, feel free to modify and import your datasets
     */
    public static void main(String[] args) {
       // try {
            List<LuceneFieldDefinition> definitions = new ArrayList<>();
        NameFrequencyDistance distance = null;
        try {
            distance = new NameFrequencyDistance(NAME_FREQUENCY_LIST);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println(distance.getDistance("stefan repcek","stefan repcik"));


    }

    public void createSimpleIndex() throws IOException {
        indexer.indexSimpleFile(new File(dataDir));
    }

    public void initSearcher() throws IOException {
        searcher.init();
    }

    public void createComplexIndex(LuceneSchema schema) throws IOException {
        indexer.indexCSVFile(new File(dataDir), schema);
    }

    public void makeSpellCheckerIndex() throws IOException {
        indexer.makeSpellCheckerIndex(SPELL_CHECKER_SOURCE_FIELD_NAME);
    }

    public void createSearch(String value, String field) throws IOException, ParseException {
        TopDocs docs = searcher.searchWithSpellchecker(value, "", fieldsToCheck);
        for (ScoreDoc scoreDoc : docs.scoreDocs) {
            Document doc = searcher.getDocument(scoreDoc);
            System.out.println(doc.getField(field).stringValue() + " score: " + scoreDoc.score);
        }
    }

    public void createMultipleSearch() throws IOException, ParseException {
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

    }
}
