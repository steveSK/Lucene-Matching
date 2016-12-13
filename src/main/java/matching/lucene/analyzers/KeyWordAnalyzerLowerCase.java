package matching.lucene.analyzers;

import org.apache.lucene.analysis.Analyzer;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.util.Version;

import java.io.Reader;

/**
 * Created by stefan on 11/10/16.
 */
public class KeyWordAnalyzerLowerCase extends Analyzer{

    public TokenStreamComponents createComponents(String field) {
        Tokenizer tokenizer = new KeywordTokenizer();
        TokenStream filter = new LowerCaseFilter(tokenizer);
        return new Analyzer.TokenStreamComponents(tokenizer,filter);
    }
}
