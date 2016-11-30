package analyzer.test.analyzers;

import org.apache.lucene.analysis.Analyzer;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ngram.NGramTokenizer;

import java.io.Reader;

/**
 * Created by stefan on 9/13/16.
 */
public class NgramAnalyzer extends Analyzer {

    private final int minGram;
    private final int maxGram;

    public NgramAnalyzer(int minGram, int maxGram){
        this.minGram = minGram;
        this.maxGram = maxGram;
    }


    @Override
    public TokenStream tokenStream(String s, Reader reader) {
        return new NGramTokenizer(reader,minGram,maxGram);
    }
}
