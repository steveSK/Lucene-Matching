package matching.lucene;

import matching.lucene.analyzers.SkipGramAnalyzerWithTokenizer;
import matching.lucene.datamining.WorldCheckAliases;
import org.apache.lucene.analysis.Analyzer;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

/**
 * Created by stefan on 12/6/16.
 */
public class DataminingMain {

    private static Analyzer analyzer = new SkipGramAnalyzerWithTokenizer(1, 3);
    private static String inputFile = "/home/stefan/matching-data/aliases-set-cleaned";
    private static String outputFPFile = "/home/stefan/matching-data/false-aliases-set";
    private static String outputTPFile = "/home/stefan/matching-data/true-aliases-set";
    private static double similarityRatio = 0.8;

    /***
     * This is the main class for generating a experimenting dataset
     *
     * @param args
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println("Start with mining: ");
        WorldCheckAliases wch = new WorldCheckAliases(analyzer,inputFile,outputFPFile,outputTPFile,similarityRatio);
        wch.generateSimilarFalseMatches();
        wch.generateSimilarTrueMatches();

    }
}
