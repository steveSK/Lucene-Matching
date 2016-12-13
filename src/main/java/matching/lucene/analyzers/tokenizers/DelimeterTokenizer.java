package matching.lucene.analyzers.tokenizers;



import org.apache.lucene.analysis.util.CharTokenizer;
import org.apache.lucene.util.AttributeFactory;

import java.io.Reader;

/**
 * Created by stefan on 11/16/16.
 */
public class DelimeterTokenizer extends CharTokenizer {


    private final Character delimeter;


    public DelimeterTokenizer(Character delimeter) {
        super();
        this.delimeter = delimeter;
    }


    @Override
    protected boolean isTokenChar(int c) {
        int intValue = (int) delimeter;
        return  intValue != c;
    }

}
