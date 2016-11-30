package matching.lucene.comparators;

import java.io.IOException;
import java.util.List;

/**
 * Created by stefan on 11/30/16.
 */
public interface StringSimiliratyComparator {

    public List<String> suggestSimilar(String word, String blockingKey, float accuracy) throws IOException;
}
