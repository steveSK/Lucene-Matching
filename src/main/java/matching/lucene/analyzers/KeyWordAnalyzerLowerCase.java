package matching.lucene.analyzers;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordTokenizer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.util.Version;

import java.io.Reader;

/**
 * Created by stefan on 11/10/16.
 */
public class KeyWordAnalyzerLowerCase extends Analyzer{

    public  KeyWordAnalyzerLowerCase(){}


    public TokenStream tokenStream(String fieldName, Reader reader) {
        return new LowerCaseFilter(Version.LUCENE_36, new KeywordTokenizer(reader));
    }
}
