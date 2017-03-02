package matching.lucene.utils;

import matching.lucene.analyzers.NgramAnalyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by stefan on 12/14/16.
 */
public class DistanceUtils {


    public static int termMatches(List<String> terms1, List<String> terms2) {
        List<String> common = new ArrayList<String>(terms1);
        common.retainAll(terms2);
        return common.size();

    }

    public static String normalizeString(String stringToNormalize) {
        return LuceneUtils.removeStopWordsFromString(stringToNormalize.toLowerCase(), SystemConstants.stopWords).trim();
    }

    public static float compareTwoStringsByNgram(String s1, String s2, NgramAnalyzer ngramAnalyzer) throws IOException {
        List<String> terms1 = new ArrayList<>();
        LuceneUtils.parseKeywords(ngramAnalyzer, "", s1).forEach(terms1::addAll);

        List<String> terms2 = new ArrayList<>();
        LuceneUtils.parseKeywords(ngramAnalyzer, "", s2).forEach(terms2::addAll);

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
