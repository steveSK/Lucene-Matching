package matching.lucene.parser;

import matching.lucene.schema.LuceneSchema;
import org.apache.lucene.document.Document;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by stefan on 10/19/16.
 */
public interface Parser {

    List<Document> parse(InputStream value, LuceneSchema schema) throws IOException;

}
