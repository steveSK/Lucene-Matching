package matching.lucene.datamining;

import matching.lucene.analyzers.NgramAnalyzer;
import matching.lucene.distances.NGramDistance;
import matching.lucene.utils.LuceneUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.spell.StringDistance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Created by stefan on 12/6/16.
 */
public class WorldCheckAliases {

    private static double minimalSimilarityforTP = 0.4;

    private final String aliasesNamesFile;
    private final String outputFPFile;
    private final String outputTPFile;
    private ArrayList<List<String>> input = new ArrayList<>();
    private final StringDistance stringDistance;
    private final double similarityTreshold;


    public WorldCheckAliases(Analyzer analyzer, String aliasesNamesFile, String outputFPFile, String outputTPFile, double similarityTreshold) throws FileNotFoundException {
        this.aliasesNamesFile = aliasesNamesFile;
        this.outputFPFile = outputFPFile;
        this.outputTPFile = outputTPFile;
        stringDistance = new NGramDistance(analyzer, new NgramAnalyzer(2, 2));
        this.similarityTreshold = similarityTreshold;
        Scanner scanner = new Scanner(new File(aliasesNamesFile)).useDelimiter("\\n");
        while (scanner.hasNext()) {
            input.add(Arrays.asList(scanner.next().split(";")));
        }
    }


    public void generateSimilarFalseMatches() throws UnsupportedEncodingException, FileNotFoundException {
        int i = 0;
        ArrayList<List<String>> listCopy = new ArrayList<>(input);
        List<String> results = new ArrayList<>();
        for (List<String> aliases : input) {
            System.out.println("Record: " + ++i);
            listCopy.remove(aliases);
            List<String> otherNames = new ArrayList<>();
            listCopy.forEach(otherNames::addAll);

            for (String s : aliases) {
                if (s.split(" ").length == 1) {
                    continue;
                }
                for (String s1 : otherNames) {
                    if (s1.split(" ").length == 1) {
                        continue;
                    }
                    if (stringDistance.getDistance(LuceneUtils.removeSpecialCharecters(s), LuceneUtils.removeSpecialCharecters(s1)) > similarityTreshold) {
                        results.add(s + " : " + s1);
                        System.out.println("Find: " + s + " : " + s1);
                    }
                }
            }
        }
        writeResults(results,outputFPFile,"False Positives:");

    }


    public void generateSimilarTrueMatches() throws UnsupportedEncodingException, FileNotFoundException {
        int i = 0;
        List<String> results = new ArrayList<>();
        for (List<String> aliases : input) {
            System.out.println("Record: " + ++i);
            for (String s : aliases) {
                if (s.split(" ").length == 1) {
                    continue;
                }
                for (int j = aliases.indexOf(s) + 1; j < aliases.size(); j++) {
                    String s1 = aliases.get(j);
                    if (s1.split(" ").length == 1) {
                        continue;
                    }
                    if (stringDistance.getDistance(LuceneUtils.removeSpecialCharecters(s), LuceneUtils.removeSpecialCharecters(s1)) > minimalSimilarityforTP) {
                        results.add(s + " : " + s1);
                        System.out.println("Find: " + s + " : " + s1);
                    }
                }
            }
        }
        writeResults(results,outputTPFile, "True Positives:");

    }


    private void writeResults(List<String> results,String outputFile, String name) throws UnsupportedEncodingException, FileNotFoundException {
        PrintWriter writer = new PrintWriter(outputFile, "UTF-8");
        writer.write(name + "\n");
        for (String res : results) {
            writer.write(res + "\n");
        }
        writer.close();
    }
}
