package matching.lucene.analyzers;

import matching.lucene.utils.LuceneUtils;
import matching.lucene.analyzers.filters.StopWordsSubStringFilter;
import matching.lucene.utils.SystemConstants;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.util.Version;

import java.io.Reader;

/**
 * Created by stefan on 11/1/16.
 */
public class KeyWordAnalyzerWithStopWords extends Analyzer{

    public TokenStreamComponents createComponents(String fieldName) {
        Tokenizer tokenizer = new KeywordTokenizer();
        TokenStream filter = new StopWordsSubStringFilter(new LowerCaseFilter(tokenizer), SystemConstants.stopWords);
        return new Analyzer.TokenStreamComponents(tokenizer,filter);
    }


}
