package matching.lucene.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by stefan on 11/30/16.
 */
public class SystemConstants {
    public static final String INDEX_SPELL_CHECKER_DIR = System.getProperty("user.home") + "/spellchecker_index";
    public static final String CSV_PARSER_DELIMETER = "\\|";
  // public static final String CSV_PARSER_DELIMETER = ",";
    public static final String INDEX_DIR = System.getProperty("user.home") + "/test_indexes";

    public final static Set<String> stopWords = new HashSet<String>(Arrays.asList(
            "international", "group", "foundation", "company", "inc", "int", "ltd", "pakistan", "stock", "holding",
            "bank", "exchange", "money", "org", "organisation", "organisations", "polyclinique", "joint", "world", "industrial",
            "industries", "technologii", "limited", "co","technologies", "army", "armée", "islamic", "russia", "industry",
            "enterprise", "consultants", "consulting", "investments", "investment", "capital", "risk","trading", "holdings",
            "sourcing", "assets", "management", "real", "estate", "properties", "finance", "union", "corp", "corporation",
            "a", "an", "and", "are", "as", "at", "be", "but", "by",
            "for", "if", "in", "into", "is", "it",
            "no", "not", "of", "on", "or", "such",
            "that", "the", "their", "then", "there", "these",
            "they", "this", "to", "was", "will", "with"
    ));
}
