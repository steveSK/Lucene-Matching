package matching.lucene.analyzers;

import matching.lucene.analyzers.tokenizers.DelimeterTokenizer;
import org.apache.lucene.analysis.Analyzer;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.miscellaneous.TrimFilter;
import org.apache.lucene.analysis.pattern.PatternReplaceFilter;
import org.apache.lucene.util.Version;


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


    public TokenStreamComponents createComponents(String fieldName) {
        Tokenizer tokenizer = new DelimeterTokenizer(delimeter);
        TokenStream trimFilter = new TrimFilter(tokenizer);
        TokenStream patternReplaceFilter = new PatternReplaceFilter(trimFilter,Pattern.compile("[^a-zA-Z0-9\\s]"), "", true);
        TokenStream lowerCaseFilter = new LowerCaseFilter(patternReplaceFilter);
        return new Analyzer.TokenStreamComponents(tokenizer,lowerCaseFilter);
    }
}
