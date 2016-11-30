package analyzer.test.analyzers.filters;

import analyzer.test.analyzers.tokenizers.SkipGramTokenizer;
import org.apache.solr.analysis.BaseTokenizerFactory;

import java.io.Reader;
import java.util.Map;

/**
 * Created by stefan on 9/23/16.
 */
public class SkipGramTokenizerFactory extends BaseTokenizerFactory {
    private int skipGramSize = 1;
    private int nGramSize = 3;

    public SkipGramTokenizerFactory() {
    }

    public void init(Map<String, String> args) {
        super.init(args);
        skipGramSize = getInt(args, "skipGramSize", SkipGramTokenizer.DEFAULT_MIN_SKIPGRAM_SIZE);
        nGramSize = getInt(args, "NGramSize", SkipGramTokenizer.DEFAULT_MAX_NGRAM_SIZE);
    }

    public SkipGramTokenizer create(Reader input) {
        return new SkipGramTokenizer(input, this.skipGramSize, this.nGramSize);
    }

    private  final int getInt(Map<String,String> args, String name, int defaultVal) {
        String s = args.remove(name);
        return s == null ? defaultVal : Integer.parseInt(s);
    }
}
