package matching.lucene.differences;

/**
 * Created by stefan on 12/14/16.
 */
public class ConsonantsDifference {

    public int getDifferece(String s1, String s2){
        return  Math.abs((getConsonantNumber(s1) - getConsonantNumber(s2)));
    }


    private int getConsonantNumber(String s){
        return  s.replaceAll("[aeiou]", "").length();
    }
}
