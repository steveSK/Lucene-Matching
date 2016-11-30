package analyzer.test.parser;

import analyzer.test.schema.LuceneFieldDefinition;
import analyzer.test.schema.LuceneSchema;
import analyzer.test.utils.LuceneUtils;
import analyzer.test.utils.SystemConstants;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by stefan on 10/19/16.
 */
public class CSVDocumentParser implements Parser {


    @Override
    public List<Document> parse(InputStream is, LuceneSchema schema) throws IOException {
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        List<Document> documents = new ArrayList<>();
        List<LuceneFieldDefinition> definitions = schema.getFieldDefinitions();
        while ((line = reader.readLine()) != null) {
            String[] split = line.split(SystemConstants.CSV_PARSER_DELIMETER,-1);
            Document document = new Document();
            int index = 0;
            for(String val : split){
                LuceneFieldDefinition definition = definitions.get(index);
                document.add(new Field(definition.getFieldName(), LuceneUtils.removeSpecialCharecters(val),definition.getFieldStore(),definition.getFieldIndex()));
                index++;
            }
            documents.add(document);
        }
        return  documents;
    }
}
