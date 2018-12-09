package matching.lucene.distances;

import matching.lucene.utils.DistanceUtils;
import matching.lucene.utils.LuceneUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.spell.StringDistance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static matching.lucene.utils.DistanceUtils.normalizeString;

/**
 * Created by stefan on 10/26/16.
 */
public class NGramDistance implements StringDistance {

    private final Analyzer mainAnalyzer;
    private final Analyzer shortAnalyzer;


    public NGramDistance(Analyzer mainAnalyzer, Analyzer shortAnalyzer) {
        this.mainAnalyzer = mainAnalyzer;
        this.shortAnalyzer = shortAnalyzer;
    }

    @Override
    public float getDistance(String s, String s1) {
        try {
            String normS = normalizeString(s).trim();
            String normS1 = normalizeString(s1).trim();
            Analyzer analyzertoUse;
            if (normS.length() < 10 && normS1.length() < 10) {
                analyzertoUse = shortAnalyzer;
            } else {
                analyzertoUse = mainAnalyzer;
            }
            List<String> terms1 = new ArrayList<>();
            LuceneUtils.parseKeywords(analyzertoUse, "", normS).forEach(terms1::addAll);
            List<String> terms2 = new ArrayList<>();
            LuceneUtils.parseKeywords(analyzertoUse, "", normS1).forEach(terms2::addAll);

            if (terms1.size() != 0 && terms2.size() != 0) {
                float shortestStringSize = 0;
                if (terms1.size() <= terms2.size()) {
                    shortestStringSize = terms1.size();
                } else {
                    shortestStringSize = terms2.size();
                }
                float matchCount = DistanceUtils.termMatches(terms1, terms2);
                return matchCount / shortestStringSize;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }


}
