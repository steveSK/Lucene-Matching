package matching.lucene.helpers;

import matching.lucene.analyzers.NgramAnalyzer;
import matching.lucene.comparators.BlockingComparator;
import matching.lucene.comparators.BruteForceComparator;
import matching.lucene.comparators.LuceneSpellCheckerComparator;
import matching.lucene.utils.LuceneUtils;
import matching.lucene.utils.RecordToMatch;
import matching.lucene.distances.NGramDistance;
import matching.lucene.comparators.StringSimiliratyComparator;
import matching.lucene.utils.SystemConstants;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Created by stefan on 9/20/16.
 */
public class SearchHelper {


    private final double minimumMatchRatio;
    private final String indexDirectoryPath;
    private final List<String> fieldsToCheck;
    private final String blockField;
    private final int topResults = 10;
    private IndexSearcher searcher;
    private final Analyzer analyzer;
    private  StringSimiliratyComparator spellChecker;
    private boolean blocking;

    public SearchHelper(String indexDirectoryPath, List<String> fieldsToCheck, Analyzer analyzer, double minimumMatchRatio,boolean blocking, String blockField) throws IOException {
        this.indexDirectoryPath = indexDirectoryPath;
        this.fieldsToCheck = fieldsToCheck;
        this.analyzer = analyzer;
        this.blockField = blockField;
        this.minimumMatchRatio = minimumMatchRatio;
        blocking = true;
    }

    public void init()  throws IOException{
        //create the reader
        IndexReader reader = IndexReader.open(FSDirectory.open(new File(indexDirectoryPath)));
        BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);
        Map<String,List<String>> blockingDict = LuceneUtils.createBlocksDictionary(fieldsToCheck,reader,blockField);
        List<String> words = LuceneUtils.readIndexField(fieldsToCheck,reader);
        searcher = new IndexSearcher(reader);
        //choose which comparator to use
         //   this.spellChecker = new LuceneSpellCheckerComparator(SystemConstants.INDEX_SPELL_CHECKER_DIR, new NGramDistance(analyzer, new NgramAnalyzer(2, 2)));
        this.spellChecker = new BlockingComparator(blockingDict, new NGramDistance(analyzer, new NgramAnalyzer(2, 2)));
       // this.spellChecker = new BruteForceComparator(words, new NGramDistance(analyzer, new NgramAnalyzer(2, 2)));
    }


    public TopDocs search(String text,String field) throws ParseException, IOException {
        return searcher.search(buildBooleanQuery(text,field), topResults);
    }

    public TopDocs searchWithSpellchecker(String text,String blockingKey, List<String> fields) throws ParseException, IOException {
       List<String> composedNames = spellChecker.suggestSimilar(text.toLowerCase(),blockingKey, (float) minimumMatchRatio);
        BooleanQuery finalQuery = new BooleanQuery();
        if(composedNames.size() != 0) {
            for (int a = 0; a < composedNames.size(); a++) {
                for(String field : fields) {
                    String value = LuceneUtils.removeSpecialCharecters(composedNames.get(a)).toLowerCase();
                    PrefixQuery prefixQuery = new PrefixQuery(new Term(field, value));
                    finalQuery.add(prefixQuery, BooleanClause.Occur.SHOULD);
                }
            }
            TopDocs result = searcher.search(finalQuery, topResults);
            return result;
        }
        return new TopDocs(0,new ScoreDoc[0],0);
    }

    public Document getDocument(ScoreDoc scoreDoc)
            throws CorruptIndexException, IOException{
        return searcher.doc(scoreDoc.doc);
    }


    private Query buildBooleanQuery(String string,String field) throws ParseException{
        List<List<String>> terms = LuceneUtils.parseKeywords(analyzer,field,string);
        BooleanQuery queryBuilder = new BooleanQuery();
        for(List<String> list : terms){
            BooleanQuery subBuilder = new BooleanQuery();
            for(String term : list){
                PhraseQuery phrase = new PhraseQuery();
                phrase.add(new Term(field,term));
                subBuilder.add(new BooleanClause(phrase, BooleanClause.Occur.SHOULD));
            }
            long minimumMatch = Math.round(list.size() * minimumMatchRatio);
            subBuilder.setMinimumNumberShouldMatch((int) minimumMatch);
            queryBuilder.add(subBuilder, BooleanClause.Occur.SHOULD);
        }
        return queryBuilder;
    }

    public Map<String,TopDocs> matchAgainstFile(String toMatchFile,List<String> fields) throws IOException, ParseException {
        try {
            Map<String,TopDocs> matchingResults = new HashMap<>();
            List<RecordToMatch> valuestoMatch = LuceneUtils.readFileWithBlockingCriteria(toMatchFile,"\\|",true);
         //   List<RecordToMatch> valuestoMatch = LuceneUtils.readFile(toMatchFile,true);
            int i = 0;
            for(RecordToMatch record : valuestoMatch){
                if(!record.getValueToMatch().isEmpty()) {
                    System.out.println("Matching record " + i + ": " + record.getValueToMatch());
                    TopDocs res = searchWithSpellchecker(record.getValueToMatch(),record.getBlockingCriteria(), fields);
                    if (res.totalHits > 0) {
                        matchingResults.put(record.getValueToMatch(),res);
                    }
                }
                i++;
            }
            return matchingResults;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }




}
