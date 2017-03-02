package matching.lucene.distances;

import matching.lucene.analyzers.DoubleMetaphoneAnalyzer;
import matching.lucene.analyzers.NgramAnalyzer;
import matching.lucene.utils.DistanceUtils;
import matching.lucene.utils.LuceneUtils;
import matching.lucene.utils.SystemConstants;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.spell.StringDistance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static matching.lucene.utils.DistanceUtils.normalizeString;

/**
 * Created by stefan on 12/14/16.
 */
public class DoubleMetaphoneDistance implements StringDistance {

    private Analyzer analyzer = new DoubleMetaphoneAnalyzer();
    private Analyzer ngramAnalyzer = new NgramAnalyzer(1,2);

    @Override
    public float getDistance(String s, String s1) {
        try {
            String normS = normalizeString(s).trim();
            String normS1 = normalizeString(s1).trim();
            List<String> split1 = Arrays.asList(normS.split(" "));
            List<String> split2 = Arrays.asList(normS1.split(" "));


            List<String> terms1 = new ArrayList<>();
            LuceneUtils.parseKeywords(analyzer, "", normS).forEach(terms1::addAll);
            terms1.removeAll(split1);

            List<String> terms2 = new ArrayList<>();
            LuceneUtils.parseKeywords(analyzer, "", normS1).forEach(terms2::addAll);
            terms2.removeAll(split2);
            if(terms1.size() == 0 || terms2.size() == 0){
                return 0;
            }
            else if(terms1.size() == terms2.size()){
                float sum = 0;
                for(int i=0;i<terms1.size();i++) {
                    String term1 = terms1.get(i);
                    String term2 = terms2.get(i);
                   // return compareTwoDoubleMethaphones(terms1.get(0), terms2.get(0));
                    sum = sum + compareTwoDoubleMethaphones(term1,term2);
                }
                return sum / terms1.size();
            }
            else{
                List<String> smallerTerms = terms1.size() < terms2.size() ? terms1 : terms2;
                List<String> largerTerms = terms1.size() > terms2.size() ? terms1 : terms2;
                float sum = 0;
                for(String small : smallerTerms){
                    float bestRatio = 0;
                    for(String large : largerTerms){
                        float ratio = compareTwoDoubleMethaphones(small,large);
                        if(ratio > bestRatio){
                            bestRatio = ratio;
                        }
                    }
                    sum = sum + bestRatio;
                }
                return  sum/smallerTerms.size();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private float compareTwoDoubleMethaphones(String dm1, String dm2) throws IOException {
        List<String> terms1 = new ArrayList<>();
        LuceneUtils.parseKeywords(ngramAnalyzer, "", dm1).forEach(terms1::addAll);

        List<String> terms2 = new ArrayList<>();
        LuceneUtils.parseKeywords(ngramAnalyzer, "", dm2).forEach(terms2::addAll);

        float shortestStringSize = 0;
        if (terms1.size() <= terms2.size()) {
            shortestStringSize = terms1.size();
        } else {
            shortestStringSize = terms2.size();
        }
        float matchCount = DistanceUtils.termMatches(terms1, terms2);
        return matchCount / shortestStringSize;
    }

}
