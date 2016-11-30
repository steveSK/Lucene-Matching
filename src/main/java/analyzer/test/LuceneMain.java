package analyzer.test;

import analyzer.test.helpers.IndexerHelper;
import analyzer.test.helpers.SearchHelper;
import analyzer.test.analyzers.*;
import analyzer.test.schema.LuceneFieldDefinition;
import analyzer.test.schema.LuceneSchema;
import analyzer.test.utils.SystemConstants;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.io.File;
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
    private static final String FILE_TO_WRITE_RESULTS = "/home/stefan/matching-data/match_results";
    private static final String BLOCK_FIELD = "countries";
    private static final String SPELL_CHECKER_SOURCE_FIELD_NAME = "name";
    private static final List<String> fieldsToCheck = new ArrayList<>();
    private static final double minimalMatchRatio = 0.75;
    private String dataDir;
    private String wordsTomatchFile;

    // private static  String dataDir = "/home/stefan/matching-data/wall-check-names-enterprise";
    // private static  String wordsTomatchFile =  "/home/stefan/matching-data/names-to-check";
    IndexerHelper indexer;
    SearchHelper searcher;

    public LuceneMain() throws IOException {
        indexer = new IndexerHelper(SystemConstants.INDEX_DIR, SystemConstants.INDEX_SPELL_CHECKER_DIR, analyzerToTest);
        indexer.clearIndex();
        searcher = new SearchHelper(SystemConstants.INDEX_DIR, fieldsToCheck, analyzerToTest, minimalMatchRatio,BLOCK_FIELD);
    }
    /*
     *   main method depends on the data feel free to modify
     */
    public static void main(String[] args) {
        try {
            List<LuceneFieldDefinition> definitions = new ArrayList<>();
            //small dataset
            /*definitions.add(new LuceneFieldDefinition("UID", Field.Store.YES, Field.Index.NOT_ANALYZED));
              definitions.add(new LuceneFieldDefinition("name", Field.Store.YES, Field.Index.ANALYZED,new KeyWordAnalyzerLowerCase()));
              definitions.add(new LuceneFieldDefinition("country", Field.Store.YES, Field.Index.ANALYZED,new StandardAnalyzer(Version.LUCENE_36)));
              definitions.add(new LuceneFieldDefinition("city", Field.Store.YES, Field.Index.ANALYZED,new StandardAnalyzer(Version.LUCENE_36)));
              definitions.add(new LuceneFieldDefinition("country_ID", Field.Store.YES, Field.Index.NOT_ANALYZED));
              definitions.add(new LuceneFieldDefinition("type_ID", Field.Store.YES, Field.Index.NOT_ANALYZED));
              definitions.add(new LuceneFieldDefinition("number_ID", Field.Store.YES, Field.Index.NOT_ANALYZED));
              definitions.add(new LuceneFieldDefinition("street", Field.Store.YES, Field.Index.ANALYZED,new StandardAnalyzer(Version.LUCENE_36)));
            */
            //big dataset
            Character del = ';';
            definitions.add(new LuceneFieldDefinition("full-name", Field.Store.YES, Field.Index.ANALYZED, new SplitAnalyzer(del)));
            definitions.add(new LuceneFieldDefinition("aliasis", Field.Store.YES, Field.Index.ANALYZED, new SplitAnalyzer(del)));
            definitions.add(new LuceneFieldDefinition("low-qal-aliasis", Field.Store.YES, Field.Index.ANALYZED, new SplitAnalyzer(del)));
            definitions.add(new LuceneFieldDefinition("countries", Field.Store.YES, Field.Index.ANALYZED, new KeyWordAnalyzerLowerCase()));
            definitions.add(new LuceneFieldDefinition("person-type", Field.Store.YES, Field.Index.ANALYZED, new KeywordAnalyzer()));
            fieldsToCheck.add("full-name");
            fieldsToCheck.add("aliasis");
            fieldsToCheck.add("low-qal-aliasis");

            LuceneMain main = new LuceneMain();
            main.wordsTomatchFile = args[0];
            main.dataDir = args[1];
            LuceneSchema schema = new LuceneSchema(definitions);
            main.createComplexIndex(schema);
            main.makeSpellCheckerIndex();
            System.out.println("Searching...");
            main.createMultipleSearch();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }


    }

    public void createSimpleIndex() throws IOException {
        indexer.indexSimpleFile(new File(dataDir));
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
        PrintWriter writer = new PrintWriter(FILE_TO_WRITE_RESULTS, "UTF-8");
        int i = 0;
        for (Map.Entry<String, TopDocs> result : results.entrySet()) {
            TopDocs topDocs = result.getValue();
            System.out.println("Result n: " + (i + 1));
            writer.println("Result n: " + (i + 1));
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document doc = searcher.getDocument(scoreDoc);
                System.out.println("Matched: " + result.getKey());
                writer.println("Matched: " + result.getKey());
                for (Fieldable field : doc.getFields()) {
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
