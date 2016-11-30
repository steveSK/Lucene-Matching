package matching.lucene.comparators;

import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.search.spell.StringDistance;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NoLockFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by stefan on 11/30/16.
 */
public class LuceneSpellCheckerComparator implements StringSimiliratyComparator {
    private SpellChecker spellChecker;
    private final static int NUM_SUG = 1000;


    public LuceneSpellCheckerComparator(String indexSpellCheckerDir, StringDistance distance) throws IOException {
        this.spellChecker = new SpellChecker(FSDirectory.open(new File(indexSpellCheckerDir), NoLockFactory.getNoLockFactory()),distance);
    }

    public List<String> suggestSimilar(String word,String blockingKey, float accuracy) throws IOException {
        return Arrays.asList(spellChecker.suggestSimilar(word, NUM_SUG, accuracy));
    }
}

