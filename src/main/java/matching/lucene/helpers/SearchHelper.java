package matching.lucene.helpers;


import matching.lucene.comparators.StringComparatorFactory;
import matching.lucene.comparators.StringComparatorType;
import matching.lucene.comparators.StringSimiliratyComparator;
import matching.lucene.utils.LuceneUtils;
import matching.lucene.utils.RecordToMatch;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.search.spell.StringDistance;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by stefan on 9/20/16.
 */
public class SearchHelper {

    private static Logger logger = LoggerFactory.getLogger(SearchHelper.class);

    private final double minimumMatchRatio;
    private final String indexDirectoryPath;
    private final List<String> fieldsToCheck;
    private final String blockField;
    private final int topResults = 10;
    private final StringComparatorType stringComparatorType;
    private final StringDistance stringDistance;
    private final Analyzer searchAnalyzer;

    private IndexSearcher searcher;
    private StringSimiliratyComparator stringComparator;
    private boolean supportSpellChecker;
    private boolean isInitialized;

    public SearchHelper(String indexDirectoryPath, Analyzer searchAnalyzer, double minimumMatchRatio) throws IOException {
        this(indexDirectoryPath,searchAnalyzer,minimumMatchRatio,null,null,null,null);
    }


    public SearchHelper(String indexDirectoryPath, Analyzer searchAnalyzer,double minimumMatchRatio,  StringDistance stringDistance, List<String> fieldsToCheck, StringComparatorType stringComparatorType, String blockField) throws IOException {
        this.indexDirectoryPath = indexDirectoryPath;
        this.fieldsToCheck = fieldsToCheck;
        this.stringDistance = stringDistance;
        this.stringComparatorType = stringComparatorType;
        this.searchAnalyzer = searchAnalyzer;
        this.blockField = blockField;
        this.minimumMatchRatio = minimumMatchRatio;
        supportSpellChecker = true;
    }

    public void init() throws IOException {
        //create the reader
        IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexDirectoryPath).toPath()));
        BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);
        searcher = new IndexSearcher(reader);
        if(supportSpellChecker) {
            stringComparator = getComparator(stringComparatorType, reader);
        }
        isInitialized = true;
    }


    public TopDocs search(String text, String field) throws IOException {
        if (!isInitialized) {
            throw new IllegalStateException("Search Helper is not initialized");
        }
        return searcher.search(buildSearchQuery(text, field), topResults);
    }

    public TopDocs searchWithSpellchecker(RecordToMatch recordToMatch) throws IOException {
        if (!isInitialized) {
            throw new IllegalStateException("Search Helper is not initialized");
        }
        if(!supportSpellChecker){
            throw new IllegalStateException("Spell Checker was not initialized");
        }
        List<String> composedNames = stringComparator.suggestSimilar(recordToMatch, (float) minimumMatchRatio);
        BooleanQuery finalQuery = new BooleanQuery();
        if (composedNames.size() != 0) {
            for (int a = 0; a < composedNames.size(); a++) {
                for (String field : fieldsToCheck) {
                    String value = LuceneUtils.removeSpecialCharecters(composedNames.get(a)).toLowerCase();
                    PrefixQuery prefixQuery = new PrefixQuery(new Term(field, value));
                    finalQuery.add(prefixQuery, BooleanClause.Occur.SHOULD);
                }
            }
            TopDocs result = searcher.search(finalQuery, topResults);
            return result;
        }
        return new TopDocs(0, new ScoreDoc[0], 0);
    }

    public Document getDocument(ScoreDoc scoreDoc) throws IOException {
        if (!isInitialized) {
            throw new IllegalStateException("Search Helper is not initialized");
        }
        return searcher.doc(scoreDoc.doc);
    }


    private Query buildSearchQuery(String string, String field) throws IOException {
        List<List<String>> terms = LuceneUtils.parseKeywords(searchAnalyzer,field, string);
        BooleanQuery queryBuilder = new BooleanQuery();
        for (List<String> list : terms) {
            BooleanQuery subBuilder = new BooleanQuery();
            for (String term : list) {
                PhraseQuery phrase = new PhraseQuery();
                phrase.add(new Term(field, term));
                subBuilder.add(new BooleanClause(phrase, BooleanClause.Occur.SHOULD));
            }
            long minimumMatch = Math.round(list.size() * minimumMatchRatio);
            subBuilder.setMinimumNumberShouldMatch((int) minimumMatch);
            queryBuilder.add(subBuilder, BooleanClause.Occur.SHOULD);
        }
        return queryBuilder;
    }

    private StringSimiliratyComparator getComparator(StringComparatorType type, IndexReader reader) throws IOException {
        switch (type) {
            case BLOCKING:
                Map<String, List<String>> blockingDict = LuceneUtils.createBlocksDictionary(fieldsToCheck, reader, blockField);
                return StringComparatorFactory.blockingComparator(blockingDict, stringDistance);
            case LUCENE:
                return StringComparatorFactory.luceneSpellChecker(indexDirectoryPath, stringDistance);
            case BRUTEFORCE:
                List<String> words = LuceneUtils.readIndexField(fieldsToCheck, reader);
                return StringComparatorFactory.bruteForce(words, stringDistance);
            default:
                throw new IllegalStateException(type + " is not supported");

        }
    }


}
