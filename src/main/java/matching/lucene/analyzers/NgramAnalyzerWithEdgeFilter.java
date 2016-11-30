package matching.lucene.analyzers;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;

import java.io.Reader;

/**
 * Created by stefan on 9/13/16.
 */
public class NgramAnalyzerWithEdgeFilter  extends Analyzer {


    @Override
    public TokenStream tokenStream(String s, Reader reader) {
        Tokenizer tokenizer = new StandardTokenizer(Version.LUCENE_36,reader);
        return new EdgeNGramTokenFilter(tokenizer,"front",3,3);
    }
}
