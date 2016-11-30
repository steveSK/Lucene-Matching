package analyzer.test.schema;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Field;

/**
 * Created by stefan on 10/19/16.
 */
public class LuceneFieldDefinition {

    private final String fieldName;
    private final Field.Store fieldStore;
    private final Field.Index fieldIndex;
    private final Analyzer fieldAnalyzer;

    public LuceneFieldDefinition(String fieldName, Field.Store fieldStore, Field.Index fieldIndex) {
        this(fieldName,fieldStore,fieldIndex,null);
    }

    public LuceneFieldDefinition(String fieldName, Field.Store fieldStore, Field.Index fieldIndex, Analyzer fieldAnalyzer) {
        this.fieldName = fieldName;
        this.fieldStore = fieldStore;
        this.fieldIndex = fieldIndex;
        this.fieldAnalyzer = fieldAnalyzer;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Field.Store getFieldStore() {
        return fieldStore;
    }

    public Field.Index getFieldIndex() {
        return fieldIndex;
    }

    public Analyzer getFieldAnalyzer() {
        return fieldAnalyzer;
    }
}
