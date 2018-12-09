package matching.lucene.helpers;

import matching.lucene.utils.LuceneUtils;
import matching.lucene.utils.RecordToMatch;
import org.apache.lucene.search.TopDocs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileMatcher {

    private static Logger logger = LoggerFactory.getLogger(SearchHelper.class);
    private final SearchHelper searchHelper;

    public FileMatcher(SearchHelper searchHelper){
        this.searchHelper = searchHelper;
    }


    public Map<String, TopDocs> matchAgainstFile(String toMatchFile, String blockingDelimeter) throws IOException {
        Map<String, TopDocs> matchingResults = new HashMap<>();
        List<RecordToMatch> valuesToMatch = LuceneUtils.readFileWithBlockingCriteria(toMatchFile, blockingDelimeter, true);

        int i = 0;
        for (RecordToMatch record : valuesToMatch) {
            if (!record.getValueToMatch().isEmpty()) {
                logger.info("Matching record " + i + ": " + record.getValueToMatch());
                TopDocs res = searchHelper.searchWithSpellchecker(record);
                if (res.totalHits > 0) {
                    matchingResults.put(record.getValueToMatch(), res);
                }
            }
            i++;
        }
        return matchingResults;
    }
}
