package matching.lucene.analyzers;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.phonetic.DoubleMetaphoneFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

/**
 * Created by stefan on 12/14/16.
 */
public class DoubleMetaphoneAnalyzer extends Analyzer {

    public TokenStreamComponents createComponents(String field) {
        Tokenizer tokenizer = new StandardTokenizer();
        TokenStream filter = new DoubleMetaphoneFilter(tokenizer, 5, true);
        return new Analyzer.TokenStreamComponents(tokenizer, filter);
    }
}
