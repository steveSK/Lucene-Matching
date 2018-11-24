package matching.lucene.comparators;

import org.apache.lucene.search.spell.StringDistance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by stefan on 11/30/16.
 */
public class BruteForceComparator implements StringSimiliratyComparator {
    private List<String> words;
    private StringDistance distance;

    public  BruteForceComparator(List<String> words, StringDistance distance){
        this.distance = distance;
        this.words = words;
    }

    @Override
    public List<String> suggestSimilar(String word, String blockingKey, float accuracy){
        List<String> suggestedWords = new ArrayList<>();
            for (String suggest : words) {
                float acc = distance.getDistance(suggest, word);
                if (acc >= accuracy) {
                    suggestedWords.add(suggest);
                }
            }
        return  suggestedWords;
    }
}
