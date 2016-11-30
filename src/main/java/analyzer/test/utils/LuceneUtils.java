package analyzer.test.utils;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;


/**
 * Created by stefan on 9/13/16.
 */
public class LuceneUtils {

    public static List<List<String>> parseKeywords(Analyzer analyzer, String field, String keywords) {
        List<List<String>> result = new ArrayList<>();
        TokenStream stream = analyzer.tokenStream(field, new StringReader(keywords));
        try {
            stream.reset();
            List<String> tempList = new ArrayList<>();
            int startOffset = 0;
            while (stream.incrementToken()) {
                OffsetAttribute offsetT = stream.getAttribute(OffsetAttribute.class);
                if (startOffset != offsetT.startOffset()) {
                    result.add(tempList);
                    tempList = new ArrayList<>();
                    startOffset = offsetT.startOffset();
                }
                tempList.add(stream.getAttribute(CharTermAttribute.class).toString());
            }
            result.add(tempList);
            stream.close();
        } catch (IOException e) {
            // not thrown b/c we're using a string reader...
        }

        return result;
    }

    public static String removeSpecialCharecters(String s) {
        return s.replaceAll("[+.^:,?/\\$()']", "");
    }

    public static String removeStopWordsFromString(String string, Set<String> stopWords) {
        String[] words = string.split(" |\\-");
        StringBuilder finalString = new StringBuilder();
        for (String word : words) {
            if (!stopWords.contains(word)) {
                finalString.append(word).append(" ");
            }
        }
        return finalString.toString();
    }

    public static List<String> readFile(String filepath, boolean removeSpecialCharacters) throws FileNotFoundException {
        Scanner s = new Scanner(new File(filepath)).useDelimiter("\\n");
        ArrayList<String> list = new ArrayList<String>();
        while (s.hasNext()) {
            if (removeSpecialCharacters) {
                list.add(LuceneUtils.removeSpecialCharecters(s.next()));
            } else {
                list.add(s.next());
            }
        }
        s.close();
        return list;
    }

    public static List<RecordToMatch> readFileWithBlockingCriteria(String filepath, String delimeter, boolean removeSpecialCharacters) throws FileNotFoundException {
        Scanner s = new Scanner(new File(filepath)).useDelimiter("\\n");
        ArrayList<RecordToMatch> list = new ArrayList<>();
        while (s.hasNext()) {
            String[] split = s.next().split(delimeter);
            if (split.length > 1) {
                RecordToMatch record;
                if (removeSpecialCharacters) {
                    record = new RecordToMatch(LuceneUtils.removeSpecialCharecters(split[0]), split[1].toLowerCase());
                } else {
                    record = new RecordToMatch(split[0], split[1]);
                }
                list.add(record);
            }
        }

        s.close();
        return list;
    }

    public static List<String> readIndexField(List<String> fields, IndexReader reader) throws IOException {
        TermEnum terms = reader.terms(new Term(fields.get(0)));
        List<String> words = new LinkedList<>();
        if (terms.term() != null) {
            do {
                Term term = terms.term();
                if (fields.contains(term.field())) {
                    words.add(term.text());
                }
            } while (terms.next());
        }
        return words;
    }

    public static Map<String, List<String>> createBlocksDictionary(List<String> fields, IndexReader reader, String blockField) throws IOException {
        Map<String, List<String>> dict = new HashMap<>();
        for (int i = 0; i < reader.maxDoc(); i++) {
            if (reader.isDeleted(i))
                continue;

            Document doc = reader.document(i);
            String key = doc.get(blockField).toLowerCase();
            if (!dict.containsKey(key)) {
                dict.put(key, new ArrayList<>());
            }
            for (String field : fields) {
                String[] values = doc.get(field).split(";");
                if (values != null && !values[0].isEmpty()) {
                    for (String value : values) {
                        dict.get(key).add(value);
                    }
                }
            }
        }
        return dict;
    }

}
