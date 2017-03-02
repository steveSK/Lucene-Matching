package matching.lucene.differences;

/**
 * Created by stefan on 12/14/16.
 */
public class CompressedLengtDifference {

    public int getDiffrence(String s1, String s2){
        String normS1 = s1.replace(" ","").replace("-","");
        String normS2 = s2.replace(" ","").replace("-","");
        return Math.abs(normS1.length() - normS2.length());
    }
}
