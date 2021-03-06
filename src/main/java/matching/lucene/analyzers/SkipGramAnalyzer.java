package matching.lucene.analyzers;

import matching.lucene.analyzers.filters.SkipGramTokenFilter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;

import java.io.Reader;

/**
 * Created by stefan on 9/14/16.
 */
public class SkipGramAnalyzer extends Analyzer {


    private final int skip;
    private final int ngram;

    public SkipGramAnalyzer(int skip,int ngram){
        this.skip = skip;
        this.ngram = ngram;
    }


    /* This is the only function that we need to override for our analyzer.
        * It takes in a java.io.Reader object and saves the tokenizer and list
        * of token filters that operate on it.
        */

    @Override
    public TokenStreamComponents createComponents(String field) {
        Tokenizer tokenizer = new StandardTokenizer();
        TokenStream lowerCase = new LowerCaseFilter(tokenizer);
        TokenStream filter = new SkipGramTokenFilter(lowerCase,skip,ngram);
        return new Analyzer.TokenStreamComponents(tokenizer,filter);
    }
}
