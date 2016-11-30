package matching.lucene.distances;

import matching.lucene.utils.LuceneUtils;
import matching.lucene.utils.SystemConstants;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.spell.StringDistance;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stefan on 10/26/16.
 */
public class NGramDistance implements StringDistance {

    private final Analyzer mainAnalyzer;
    private final Analyzer shortAnalyzer;


    public NGramDistance(Analyzer mainAnalyzer, Analyzer shortAnalyzer){
        this.mainAnalyzer = mainAnalyzer;
        this.shortAnalyzer = shortAnalyzer;
    }

    private int gramMatches(List<String> grams1, List<String> grams2){
        List<String> common = new ArrayList<String>(grams1);
        common.retainAll(grams2);
        return common.size();

    }

    @Override
    public float getDistance(String s, String s1) {
        String normS = normalizeString(s).trim();
        String normS1 = normalizeString(s1).trim();
        Analyzer analyzertoUse;
        if(normS.length() < 10 && normS1.length() < 10){
            analyzertoUse = shortAnalyzer;
        }
        else{
            analyzertoUse = mainAnalyzer;
        }
        List<String> terms1 = new ArrayList<>();
        LuceneUtils.parseKeywords( analyzertoUse ,"",normS).forEach(terms1::addAll);

        List<String> terms2 = new ArrayList<>();
        LuceneUtils.parseKeywords( analyzertoUse , "", normS1).forEach(terms2::addAll);
        if(terms1.size() != 0 && terms2.size() != 0) {
            float shortestStringSize = 0;
            if (terms1.size() <= terms2.size()) {
                shortestStringSize = terms1.size();
            } else {
                shortestStringSize = terms2.size();
            }
            float matchCount = gramMatches(terms1, terms2);
            return matchCount / shortestStringSize;
        }
        return 0;
    }

    private String normalizeString(String stringToNormalize){
       return LuceneUtils.removeStopWordsFromString(stringToNormalize.toLowerCase(), SystemConstants.stopWords);
    }
}