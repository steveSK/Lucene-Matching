package matching.lucene.utils;

import matching.lucene.helpers.SearchHelper;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;


/**
 * Created by stefan on 9/13/16.
 */
public final class LuceneUtils {

    private static Logger logger = LoggerFactory.getLogger(LuceneUtils.class);

    public static List<List<String>> parseKeywords(Analyzer analyzer, String field, String keywords) throws IOException {
        List<List<String>> result = new ArrayList<>();
        TokenStream stream = analyzer.tokenStream(field, new StringReader(keywords));
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

    public static List<RecordToMatch> readFile(String filepath, boolean removeSpecialCharacters) throws FileNotFoundException {
        Scanner s = new Scanner(new File(filepath)).useDelimiter("\\n");
        ArrayList<RecordToMatch> list = new ArrayList<>();
        while (s.hasNext()) {
            if (removeSpecialCharacters) {
                list.add(new RecordToMatch(LuceneUtils.removeSpecialCharecters(s.next())));
            } else {
                list.add(new RecordToMatch(s.next()));
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
            if (split.length == 2) {
                RecordToMatch record;
                if (removeSpecialCharacters) {
                    record = new RecordToMatch(LuceneUtils.removeSpecialCharecters(split[0]), split[1].toLowerCase());
                } else {
                    record = new RecordToMatch(split[0], split[1]);
                }
                list.add(record);
            } else {
                logger.warn("Record to match does not have a 2 fields");
            }
        }

        s.close();
        return list;
    }

    public static List<String> readIndexField(List<String> fields0, IndexReader reader) throws IOException {

        Fields fields = MultiFields.getFields(reader);
        List<String> words = new LinkedList<>();
        for (String field : fields) {
            if (fields0.contains(field)) {
                Terms terms = fields.terms(field);
                TermsEnum termsEnum = terms.iterator();
                while (termsEnum.next() != null) {
                    words.add(new String(termsEnum.term().bytes, "UTF-8"));
                }
            }
        }
        return words;
    }

    public static Map<String, List<String>> createBlocksDictionary(List<String> fields, IndexReader reader, String blockField) throws IOException {
        Map<String, List<String>> dict = new HashMap<>();
        for (int i = 0; i < reader.maxDoc(); i++) {
            Document doc = reader.document(i);
            System.out.println(doc);
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
