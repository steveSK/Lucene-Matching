package matching.lucene.analyzers.filters;

import matching.lucene.analyzers.tokenizers.SkipGramTokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeFactory;

import java.io.Reader;
import java.util.Map;

/**
 * Created by stefan on 9/23/16.
 */
public class SkipGramTokenizerFactory extends TokenizerFactory {
    private int skipGramSize = 1;
    private int nGramSize = 3;

    public SkipGramTokenizerFactory(Map<String, String> args){
        super(args);
        skipGramSize = getInt(args, "skipGramSize", SkipGramTokenizer.DEFAULT_MIN_SKIPGRAM_SIZE);
        nGramSize = getInt(args, "NGramSize", SkipGramTokenizer.DEFAULT_MAX_NGRAM_SIZE);
    }

    @Override
    public SkipGramTokenizer create(AttributeFactory factory) {
        return new SkipGramTokenizer(factory, this.skipGramSize, this.nGramSize);
    }
}
