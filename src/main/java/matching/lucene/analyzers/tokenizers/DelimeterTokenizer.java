package matching.lucene.analyzers.tokenizers;

import org.apache.lucene.analysis.CharTokenizer;
import org.apache.lucene.util.Version;

import java.io.Reader;

/**
 * Created by stefan on 11/16/16.
 */
public class DelimeterTokenizer extends CharTokenizer {


    private final Character delimeter;


    public DelimeterTokenizer(Character delimeter, Reader reader) {
        super(Version.LUCENE_36, reader);
        this.delimeter = delimeter;
    }


    @Override
    protected boolean isTokenChar(int c) {
        int intValue = (int) delimeter;
        return  intValue != c;
    }

}
