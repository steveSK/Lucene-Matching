package matching.lucene.analyzers.tokenizers;

import matching.lucene.utils.CharacterUtils;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by stefan on 9/15/16.
 */
public class SkipGramTokenizer extends Tokenizer {
    public static final int DEFAULT_MIN_SKIPGRAM_SIZE = 1;
    public static final int DEFAULT_MAX_NGRAM_SIZE = 2;

    private CharacterUtils.CharacterBuffer charBuffer;
    private CharacterUtils charUtils;
    private int[] buffer; // like charBuffer, but converted to code points
    private int bufferStart, bufferEnd; // remaining slice in buffer
    private int offset;
    private boolean skip;
    private int currSkipOffset;
    private int skipGram, nGram;
    private boolean isFinished;
    private boolean exhausted;
    private int lastCheckedChar; // last offset in the buffer that we checked
    private int lastNonTokenChar; // last offset that we found to not be a token char

    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final PositionIncrementAttribute posIncAtt = addAttribute(PositionIncrementAttribute.class);
    private final PositionLengthAttribute posLenAtt = addAttribute(PositionLengthAttribute.class);
    private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);

    public SkipGramTokenizer(Reader reader, int skipGram, int nGram) {
        super(reader);
        init(skipGram, nGram);
    }


    public SkipGramTokenizer(AttributeFactory factory, Reader reader, int skipGram, int nGram, boolean edgesOnly) {
        super(factory,reader);
        init(skipGram, nGram);
    }

    /**
     * Creates NGramTokenizer with given min and max n-grams.
     * @param factory
     * @param skipGram the smallest n-gram to generate
     * @param nGram the largest n-gram to generate
     */
    public SkipGramTokenizer(AttributeFactory factory, Reader reader, int skipGram, int nGram) {
        this(factory, reader, skipGram, nGram, false);
    }

    /**
     * Creates NGramTokenizer with default min and max n-grams.
     */
    public SkipGramTokenizer(Reader reader) {
        this(reader, DEFAULT_MIN_SKIPGRAM_SIZE, DEFAULT_MAX_NGRAM_SIZE);
    }

    private void init(int skipGram, int maxGram) {
        if (skipGram < 1) {
            throw new IllegalArgumentException("skipGram must be greater than zero");
        }
        if (skipGram > maxGram) {
            throw new IllegalArgumentException("skipGram must not be greater than nGram");
        }
        this.skipGram = maxGram;
        this.nGram = maxGram;
        charBuffer = CharacterUtils.newCharacterBuffer(2 * maxGram + 1024); // 2 * nGram in case all code points require 2 chars and + 1024 for buffering to not keep polling the Reader
        buffer = new int[charBuffer.getBuffer().length];
        currSkipOffset = 1;
        // Make the term att large enough
        termAtt.resizeBuffer(2 * maxGram);
    }

    @Override
    public final boolean incrementToken() throws IOException {
        clearAttributes();
        if(isFinished){
            return false;
        }
        // termination of this loop is guaranteed by the fact that every iteration
        // either advances the buffer (calls consumes()) or increases gramSize
        while (true) {
            // compact
            if (bufferStart >= bufferEnd - nGram - 1 && !exhausted) {
                System.arraycopy(buffer, bufferStart, buffer, 0, bufferEnd - bufferStart);
                bufferEnd -= bufferStart;
                lastCheckedChar -= bufferStart;
                lastNonTokenChar -= bufferStart;
                bufferStart = 0;


                // fill in remaining space
                exhausted = !charUtils.fill(charBuffer, input, buffer.length - bufferEnd);
                // convert to code points
                bufferEnd += charUtils.toCodePoints(charBuffer.getBuffer(), 0, charBuffer.getLength(), buffer, bufferEnd);
                offsetAtt.setOffset(bufferStart,bufferEnd);
                if(charBuffer.getLength() <= nGram){
                    charUtils.toChars(buffer, bufferStart, charBuffer.getLength(), termAtt.buffer(), 0);
                    termAtt.setLength(charBuffer.getLength());
                    isFinished = true;
                    return true;
                }
                if(bufferStart == bufferEnd){
                    return false;
                }
            }

            updateLastNonTokenChar();

            // retry if the token to be emitted was going to not only contain token chars
            final boolean termContainsNonTokenChar = lastNonTokenChar >= bufferStart && lastNonTokenChar < (bufferStart + nGram);
            if (termContainsNonTokenChar) {
                consume();
                continue;
            }
            posIncAtt.setPositionIncrement(1);
            posLenAtt.setPositionLength(1);
            if(!skip) {
                final int length = charUtils.toChars(buffer, bufferStart, nGram, termAtt.buffer(), 0);
                termAtt.setLength(length);
              //  offsetAtt.setOffset(correctOffset(offset), correctOffset(offset + length));
                skip = true;
            }
            else if(bufferStart + nGram < bufferEnd) {
                final int length = charUtils.toChars(buffer, bufferStart, currSkipOffset, termAtt.buffer(), 0);
                int sufix= nGram - currSkipOffset;
                char[] tempBuffer = new char[sufix];
                termAtt.setLength(length);
                charUtils.toChars(buffer, bufferStart + currSkipOffset + 1, sufix, tempBuffer, 0);
                termAtt.append(new String(tempBuffer));
          //      offsetAtt.setOffset(correctOffset(offset), correctOffset(offset + nGram + skipGram));
                currSkipOffset++;
            }
            else{
                return false;
            }
            if (skip && currSkipOffset >= nGram || bufferStart + nGram > bufferEnd) {
                skip = false;
                currSkipOffset = 1;
                consume();
            }
            return true;
        }
    }

    private void updateLastNonTokenChar() {
        final int termEnd = bufferStart + nGram - 1;
        if (termEnd > lastCheckedChar) {
            for (int i = termEnd; i > lastCheckedChar; --i) {
                if (!isTokenChar(buffer[i])) {
                    lastNonTokenChar = i;
                    break;
                }
            }
            lastCheckedChar = termEnd;
        }
    }

    /** Consume one code point. */
    private void consume() {
        offset += Character.charCount(buffer[bufferStart++]);
    }

    /** Only collect characters which satisfy this condition. */
    protected boolean isTokenChar(int chr) {
        return true;
    }

    @Override
    public final void end() throws IOException {
        super.end();
        assert bufferStart <= bufferEnd;
        int endOffset = offset;
        for (int i = bufferStart; i < bufferEnd; ++i) {
            endOffset += Character.charCount(buffer[i]);
        }
        endOffset = correctOffset(endOffset);
        // set final offset
    //    offsetAtt.setOffset(endOffset, endOffset);
    }

    @Override
    public final void reset() throws IOException {
        super.reset();
        bufferStart = bufferEnd = buffer.length;
        lastNonTokenChar = lastCheckedChar = bufferStart - 1;
        offset = 0;
        exhausted = false;
        charBuffer.reset();
    }
}
