package matching.lucene.analyzers.filters;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.solr.analysis.BaseTokenFilterFactory;

import java.util.Map;

/**
 * Created by stefan on 9/15/16.
 */
public class SkipGramFilterFactory extends  BaseTokenFilterFactory {
    private final int NGramSize;
    private final int skipGramSize;

    /** Creates a new SkipGramFilterFactory */
    public SkipGramFilterFactory(Map<String, String> args) {
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

    private  final int getInt(Map<String,String> args, String name, int defaultVal) {
        String s = args.remove(name);
        return s == null ? defaultVal : Integer.parseInt(s);
    }
}
