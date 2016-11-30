package matching.lucene.comparators;

import org.apache.lucene.search.spell.StringDistance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by stefan on 11/2/16.
 */
public class BruteForceComparator implements StringSimiliratyComparator {
    private Map<String,List<String>> blockingDictonary;
    private StringDistance distance;

    public BruteForceComparator(Map<String,List<String>> blockingDictonary, StringDistance distance){
            this.distance = distance;
            this.blockingDictonary = blockingDictonary;
    }

    public List<String> suggestSimilar(String word,String blockingKey, float accuracy){
        List<String> suggestedWords = new ArrayList<>();
        List<String>  block = blockingDictonary.get(blockingKey);
        if(block!= null) {
            for (String suggest : block) {
                float acc = distance.getDistance(suggest, word);
                if (acc >= accuracy) {
                    suggestedWords.add(suggest);
                }
            }
        }
        return  suggestedWords;
    }
}
