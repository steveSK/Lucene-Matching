package matching.lucene.distances;

import matching.lucene.analyzers.NgramAnalyzer;
import matching.lucene.utils.DistanceUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.spell.StringDistance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import static matching.lucene.utils.DistanceUtils.normalizeString;

/**
 * Created by stefan on 12/14/16.
 */
public class NameFrequencyDistance implements StringDistance {

    private Map<String, Integer> nameFreqMap = new HashMap<>();
    private Analyzer ngramAnalyzer = new NgramAnalyzer(1, 2);

    public NameFrequencyDistance(String nameFreqFile) throws FileNotFoundException {
        Scanner s = new Scanner(new File(nameFreqFile)).useDelimiter("\\n");
        while (s.hasNext()) {
            String[] split = s.next().split(",");
            nameFreqMap.put(split[0], Integer.valueOf(split[1]));
        }
    }

    @Override
    public float getDistance(String s, String s1) {
        try {
            String normS1 = normalizeString(s);
            String normS2 = normalizeString(s1);
            String[] smallerString = normS1.split(" ").length < normS2.split(" ").length ? normS1.split(" ") : normS2.split(" ");
            String[] largerString = normS1.split(" ").length > normS2.split(" ").length ? normS1.split(" ") : normS2.split(" ");

            int sum = 0;
            for (String small : smallerString) {
                float bestRatio = 0;
                String bestMatch = "";
                for (String large : largerString) {
                    float ratio = DistanceUtils.compareTwoStringsByNgram(small, large, (NgramAnalyzer) ngramAnalyzer);
                    if (ratio > bestRatio) {
                        bestRatio = ratio;
                        bestMatch = large;
                    }
                }
                int frequency1 = 1;
                int frequency2 = 1;
                if(nameFreqMap.get(small) != null){
                    frequency1 = nameFreqMap.get(small);
                }
                if(nameFreqMap.get(bestMatch) != null){
                    frequency2 = nameFreqMap.get(bestMatch);
                }
                sum += (frequency1 + frequency2)/2;
            }
            return sum / smallerString.length;
        } catch (IOException e) {
            e.printStackTrace();
            return  0;
        }
    }


}
