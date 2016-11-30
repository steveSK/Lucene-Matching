package matching.lucene.schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by stefan on 10/19/16.
 */
public class LuceneSchema {

    private final List<LuceneFieldDefinition> fieldDefinitions;

    public LuceneSchema(List<LuceneFieldDefinition> fieldDefinitions) {
        this.fieldDefinitions = new ArrayList<>(fieldDefinitions);
    }

    public List<LuceneFieldDefinition> getFieldDefinitions() {
        return Collections.unmodifiableList(fieldDefinitions);
    }
}
