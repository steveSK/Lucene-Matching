package matching.lucene.analyzers;

import matching.lucene.analyzers.tokenizers.DelimeterTokenizer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.util.Version;
import org.apache.solr.analysis.PatternReplaceFilter;
import org.apache.solr.analysis.TrimFilter;

import java.io.Reader;
import java.util.regex.Pattern;

/**
 * Created by stefan on 11/16/16.
 */
public class SplitAnalyzer extends Analyzer {

    private Character delimeter;

    public  SplitAnalyzer(Character delimeter){
        this.delimeter = delimeter;
    }


    public TokenStream tokenStream(String fieldName, Reader reader) {
        return new LowerCaseFilter(Version.LUCENE_36, new PatternReplaceFilter(new TrimFilter(new DelimeterTokenizer(delimeter,reader),true),Pattern.compile("[^a-zA-Z0-9\\s]"), "", true));
    }
}
