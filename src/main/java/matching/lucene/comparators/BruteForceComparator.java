package matching.lucene.comparators;

import matching.lucene.utils.RecordToMatch;
import org.apache.lucene.search.spell.StringDistance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by stefan on 11/30/16.
 */
public class BruteForceComparator implements StringSimiliratyComparator {
    private final List<String> words;
    private final StringDistance distance;

    public  BruteForceComparator(List<String> words, StringDistance distance){
        this.distance = distance;
        this.words = words;
    }

    @Override
    public List<String> suggestSimilar(RecordToMatch recordToMatch, float accuracy){
        List<String> suggestedWords = new ArrayList<>();
            for (String suggest : words) {
                float acc = distance.getDistance(suggest, recordToMatch.getValueToMatch());
                if (acc >= accuracy) {
                    suggestedWords.add(suggest);
                }
            }
        return  suggestedWords;
    }
}
