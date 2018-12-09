package matching.lucene.comparators;

import matching.lucene.utils.RecordToMatch;

import java.io.IOException;
import java.util.List;

/**
 * Created by stefan on 11/30/16.
 */
public interface StringSimiliratyComparator {

    List<String> suggestSimilar(RecordToMatch recordToMatch, float accuracy) throws IOException;
}
