package matching.lucene.utils;

/**
 * Created by stefan on 11/28/16.
 */
public class RecordToMatch {

    private final String valueToMatch;
    private final String blockingCriteria;

    public RecordToMatch(String valueToMatch, String blockingCriteria) {
        this.valueToMatch = valueToMatch;
        this.blockingCriteria = blockingCriteria;
    }

    public String getValueToMatch() {
        return valueToMatch;
    }

    public String getBlockingCriteria() {
        return blockingCriteria;
    }
}
