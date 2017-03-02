package matching.lucene.distances;

import org.apache.lucene.search.spell.StringDistance;

import static matching.lucene.utils.DistanceUtils.normalizeString;

/**
 * Created by stefan on 12/14/16.
 */
public class LCSDistance implements StringDistance {
    @Override
    public float getDistance(String s, String s1) {
        String normS1 = normalizeString(s);
        String normS2 = normalizeString(s1);
        float shortLenght = normS1.length() > normS2.length() ? normS2.length() : normS1.length();
        float sum = 0;
        while (true){
            String longestSubs = longestSubstring(normS1,normS2);
            if(longestSubs.isEmpty()){
                return sum / shortLenght;
            }
            sum += longestSubs.length();
            normS1 = normS1.replace(longestSubs,"");
            normS2 = normS2.replace(longestSubs,"");
        }
    }





    private  String longestSubstring(String str1, String str2) {
        StringBuilder sb = new StringBuilder();
        if (str1 == null || str1.isEmpty() || str2 == null || str2.isEmpty())
            return "";

        int[][] num = new int[str1.length()][str2.length()];
        int maxlen = 0;
        int lastSubsBegin = 0;

        for (int i = 0; i < str1.length(); i++) {
            for (int j = 0; j < str2.length(); j++) {
                if (str1.charAt(i) == str2.charAt(j)) {
                    if ((i == 0) || (j == 0))
                        num[i][j] = 1;
                    else
                        num[i][j] = 1 + num[i - 1][j - 1];
                    if (num[i][j] > maxlen) {
                        maxlen = num[i][j];
                        // generate substring from str1 => i
                        int thisSubsBegin = i - num[i][j] + 1;
                        if (lastSubsBegin == thisSubsBegin) {
                            //if the current LCS is the same as the last time this block ran
                            sb.append(str1.charAt(i));
                        } else {
                            //this block resets the string builder if a different LCS is found
                            lastSubsBegin = thisSubsBegin;
                            sb = new StringBuilder();
                            sb.append(str1.substring(lastSubsBegin, i + 1));
                        }
                    }
                }
            }}

        return sb.toString();
    }
}
