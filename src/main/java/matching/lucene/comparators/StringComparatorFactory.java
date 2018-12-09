package matching.lucene.comparators;

import org.apache.lucene.search.spell.StringDistance;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class StringComparatorFactory {



    public static BruteForceComparator bruteForce(List<String> words, StringDistance distance){
        return new BruteForceComparator(words,distance);
    }

    public static LuceneSpellCheckerComparator luceneSpellChecker(String indexSpellCheckerDir, StringDistance distance) throws IOException {
        return new LuceneSpellCheckerComparator(indexSpellCheckerDir,distance);
    }

    public static BlockingComparator blockingComparator(Map<String,List<String>> blockingDictonary, StringDistance distance){
        return new BlockingComparator(blockingDictonary,distance);

    }



}
