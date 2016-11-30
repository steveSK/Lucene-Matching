package analyzer.test.analyzers.filters;

import java.io.IOException;
import java.util.Arrays;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;


/**
 * Created by stefan on 9/14/16.
 */
public class SkipGramTokenFilter extends TokenFilter{
    public static final int DEFAULT_MIN_SKIPGRAM_SIZE = 1;
    public static final int DEFAULT_MAX_NGRAM_SIZE = 2;

    private final int skipGram, nGram;

    private char[] curTermBuffer;
    private int curTermLength;
    private int curCodePointCount;
    private int curPos;
    private boolean skip;
    private int currSkipOffset;
    private int curPosInc, curPosLen;
    private int tokStart;
    private int tokEnd;

    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final PositionIncrementAttribute posIncAtt;
    private final PositionLengthAttribute posLenAtt;
    private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);

    /**
     * Creates NGramTokenFilter with given min and max n-grams.
     * @param input {@link TokenStream} holding the input to be tokenized
     * @param minGram the smallest n-gram to generate
     * @param nGram the largest n-gram to generate
     */
    public SkipGramTokenFilter(TokenStream input, int minGram, int nGram) {
        super(input);
        if (minGram < 1) {
            throw new IllegalArgumentException("skipGram must be greater than zero");
        }
        if (minGram > nGram) {
            throw new IllegalArgumentException("skipGram must not be greater than nGram");
        }
        this.skipGram = minGram;
        this.nGram = nGram;

        posIncAtt = addAttribute(PositionIncrementAttribute.class);
        posLenAtt = addAttribute(PositionLengthAttribute.class);
    }

    /**
     * Creates NGramTokenFilter with default min and max n-grams.
     * @param input {@link TokenStream} holding the input to be tokenized
     */
    public SkipGramTokenFilter(TokenStream input) {
        this(input, DEFAULT_MIN_SKIPGRAM_SIZE, DEFAULT_MAX_NGRAM_SIZE);
    }

    /** Returns the next token in the stream, or null at EOS. */
    @Override
    public final boolean incrementToken() throws IOException {
        while (true) {
            if (curTermBuffer == null) {
                if (!input.incrementToken()) {
                    return false;
                } else {
                    curTermBuffer = termAtt.buffer().clone();
                    curTermLength = termAtt.length();
                    curCodePointCount = Character.codePointCount(termAtt, 0, termAtt.length());
                    curPos = 0;
                    currSkipOffset = 1;
                    skip = false;
                    curPosInc = posIncAtt.getPositionIncrement();
                    curPosLen = posLenAtt.getPositionLength();
                    tokStart = offsetAtt.startOffset();
                    tokEnd = offsetAtt.endOffset();
                }
            }

            if ((curPos + nGram) <= curCodePointCount) {
                clearAttributes();
                final int start = Character.offsetByCodePoints(curTermBuffer, 0, curTermLength, 0, curPos);
                final int end = Character.offsetByCodePoints(curTermBuffer, 0, curTermLength, start, nGram);
                posIncAtt.setPositionIncrement(curPosInc);
                curPosInc = 0;
                posLenAtt.setPositionLength(curPosLen);
                offsetAtt.setOffset(tokStart, tokEnd);
                if(!skip) {
                    termAtt.copyBuffer(curTermBuffer, start, end - start);
                    skip = true;
                    return true;
                }
                else if((curPos + nGram + skipGram) <= curCodePointCount){
                    termAtt.copyBuffer(curTermBuffer,start,currSkipOffset);
                    termAtt.append(new String(Arrays.copyOfRange(curTermBuffer,start + currSkipOffset + 1, end + skipGram)));
                    currSkipOffset++;
                    if(currSkipOffset >= nGram){
                        skip = false;
                        currSkipOffset = 1;
                        curPos++;
                    }
                    return true;
                }

            }
            curTermBuffer = null;
        }
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        curTermBuffer = null;
    }
}
