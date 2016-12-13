package matching.lucene.analyzers.filters;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.TokenFilterFactory;

import java.util.Map;

/**
 * Created by stefan on 9/15/16.
 */
public class SkipGramFilterFactory extends TokenFilterFactory {
    private final int NGramSize;
    private final int skipGramSize;

    /** Creates a new SkipGramFilterFactory */
    public SkipGramFilterFactory(Map<String, String> args) {
        super(args);
        skipGramSize = getInt(args, "skipGramSize", SkipGramTokenFilter.DEFAULT_MIN_SKIPGRAM_SIZE);
        NGramSize = getInt(args, "NGramSize", SkipGramTokenFilter.DEFAULT_MAX_NGRAM_SIZE);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public TokenFilter create(TokenStream input) {
        return new SkipGramTokenFilter(input, skipGramSize, NGramSize);
    }
}
