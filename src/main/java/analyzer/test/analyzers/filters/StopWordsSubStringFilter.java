package analyzer.test.analyzers.filters;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.util.Set;

/**
 * Created by stefan on 11/1/16.
 */
public class StopWordsSubStringFilter extends TokenFilter {

    protected CharTermAttribute charTermAttribute = addAttribute(CharTermAttribute.class);
    private Set<String> stopWords;

    public StopWordsSubStringFilter(TokenStream input,Set<String> stopWords){
        super(input);
        this.stopWords = stopWords;

    }

    @Override
    public boolean incrementToken() throws IOException {

        if (!this.input.incrementToken()) {
            return false;
        }

        String currentTokenInStream = this.input.getAttribute(CharTermAttribute.class).toString();
        StringBuilder finalToken = new StringBuilder();
        String[] words = currentTokenInStream.split(" ");
        for(String word : words){
            if(!stopWords.contains(word)){
                finalToken.append(word).append(" ");
            }
        }

        charTermAttribute.setEmpty().append(finalToken.toString());

        return true;
    }
}
