package matching.lucene.distances;

import org.apache.lucene.search.spell.StringDistance;

import java.util.Arrays;

/**
 * Created by stefan on 12/14/16.
 */
public class JaroWinklerStringDistance implements StringDistance{

    private final float TRESHOLD = 0.7F;

    private int[] matches(String s1, String s2) {
        String max, min;
        if (s1.length() > s2.length()) {
            max = s1;
            min = s2;
        } else {
            max = s2;
            min = s1;
        }
        int range = Math.max(max.length() / 2 - 1, 0);
        int[] matchIndexes = new int[min.length()];
        Arrays.fill(matchIndexes, -1);
        boolean[] matchFlags = new boolean[max.length()];
        int matches = 0;

        for (int mi = 0; mi < min.length(); mi++) {
            char c1 = min.charAt(mi);
            for (int xi = Math.max(mi - range, 0), xn = Math.min(mi + range + 1, max.length()); xi < xn; xi++) {
                if (!matchFlags[xi] && c1 == max.charAt(xi)) {
                    matchIndexes[mi] = xi;
                    matchFlags[xi] = true;
                    matches++;
                    break;
                }
            }
        }

        char[] ms1 = new char[matches];
        char[] ms2 = new char[matches];
        for (int i = 0, si = 0; i < min.length(); i++) {
            if (matchIndexes[i] != -1) {
                ms1[si] = min.charAt(i);
                si++;
            }
        }

        for (int i = 0, si = 0; i < max.length(); i++) {
            if (matchFlags[i]) {
                ms2[si] = max.charAt(i);
                si++;
            }
        }

        int transpositions = 0;
        for (int mi = 0; mi < ms1.length; mi++) {
            if (ms1[mi] != ms2[mi]) {
                transpositions++;
            }
        }

        int prefix = 0;
        for (int mi = 0; mi < min.length(); mi++) {
            if (s1.charAt(mi) == s2.charAt(mi)) {
                prefix++;
            } else {
                break;
            }
        }
        return new int[]{matches, transpositions / 2, prefix, max.length()};
    }

    public float getDistance(String s1, String s2) {
        int[] mtp = matches(s1, s2);
        int l = mtp[2];
        if (l > 4) {
            l = 4;
        }

        float m = mtp[0];
        if (m == 0) {
            return 0f;
        }
        float j = ((m / s1.length() + m / s2.length() + (m - mtp[1]) / m)) / 3;
        float jw = j < TRESHOLD ? j : j + 0.1f * l * (1 - j);
        return jw;
    }
}


