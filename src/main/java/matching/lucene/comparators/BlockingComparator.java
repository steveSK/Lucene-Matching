package matching.lucene.comparators;

import matching.lucene.utils.RecordToMatch;
import org.apache.lucene.search.spell.StringDistance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by stefan on 11/2/16.
 */
public class BlockingComparator implements StringSimiliratyComparator {


    private final Map<String,List<String>> blockingDictonary;
    private final StringDistance distance;
    public BlockingComparator(Map<String,List<String>> blockingDictonary, StringDistance distance){
        this.blockingDictonary = blockingDictonary;
        this.distance = distance;
    }

    @Override
    public List<String> suggestSimilar(RecordToMatch recordToMatch, float accuracy){
        List<String> suggestedWords = new ArrayList<>();
        List<String>  block = blockingDictonary.get(recordToMatch.getBlockingCriteria());
        if(block!= null) {
            for (String suggest : block) {
                float acc = distance.getDistance(suggest, recordToMatch.getValueToMatch());
                if (acc >= accuracy) {
                    suggestedWords.add(suggest);
                }
            }
        }
        return  suggestedWords;
    }
}
