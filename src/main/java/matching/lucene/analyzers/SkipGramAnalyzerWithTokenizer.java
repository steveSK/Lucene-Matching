package matching.lucene.analyzers;

import matching.lucene.analyzers.tokenizers.SkipGramTokenizer;
import org.apache.lucene.analysis.*;
import org.apache.lucene.util.Version;

import java.io.Reader;

/**
 * Created by stefan on 9/15/16.
 */
public class SkipGramAnalyzerWithTokenizer extends Analyzer {


    private final int skip;
    private final int ngram;

    public SkipGramAnalyzerWithTokenizer(int skip,int ngram){
        this.skip = skip;
        this.ngram = ngram;
    }

    /* This is the only function that we need to override for our analyzer.
        * It takes in a java.io.Reader object and saves the tokenizer and list
        * of token filters that operate on it.
        */
    @Override
    public TokenStream tokenStream(String s, Reader reader) {
        Tokenizer tokenizer = new SkipGramTokenizer(reader, skip,ngram);
        return new LowerCaseFilter(Version.LUCENE_36,tokenizer);
    }
}
