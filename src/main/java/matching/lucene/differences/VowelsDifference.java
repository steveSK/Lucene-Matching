package matching.lucene.differences;

/**
 * Created by stefan on 12/14/16.
 */
public class VowelsDifference {


    public int getDifferece(String s1, String s2){
        return  Math.abs((getVowelsNumber(s1) - getVowelsNumber(s2)));
    }


    private int getVowelsNumber(String s){
        return  s.length() - s.replaceAll("[aeiou]", "").length();
    }
}
