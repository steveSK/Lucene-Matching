package analyzer.test.analyzers;

import analyzer.test.utils.LuceneUtils;
import analyzer.test.analyzers.filters.StopWordsSubStringFilter;
import org.apache.lucene.analysis.*;
import org.apache.lucene.util.Version;

import java.io.Reader;

/**
 * Created by stefan on 11/1/16.
 */
public class KeyWordAnalyzerWithStopWords extends Analyzer{

    public KeyWordAnalyzerWithStopWords () {
    }

    public TokenStream  tokenStream(String fieldName, Reader reader) {
        TokenStream tokenizer = new KeywordTokenizer(reader);
        return new StopWordsSubStringFilter(new LowerCaseFilter(Version.LUCENE_36,tokenizer), LuceneUtils.stopWords);
    }


}
