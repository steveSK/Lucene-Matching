package matching.lucene.helpers;

import matching.lucene.parser.CSVDocumentParser;
import matching.lucene.parser.Parser;
import matching.lucene.schema.LuceneFieldDefinition;
import matching.lucene.schema.LuceneSchema;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.*;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NoLockFactory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static org.apache.lucene.document.Field.*;

/**
 * Created by stefan on 9/18/16.
 */
public class IndexerHelper {

    private Analyzer mainAnalyzer;
    private Directory indexDirectory;
    private Directory spellCheckerDir;

    public IndexerHelper(String indexDirectoryPath,String spellCheckerDirPath, Analyzer analyzer) throws IOException {
        //this directory will contain the indexes
        this.indexDirectory =  FSDirectory.open(new File(indexDirectoryPath));
        this.mainAnalyzer = analyzer;
        this.spellCheckerDir = FSDirectory.open(new File(spellCheckerDirPath), NoLockFactory.getNoLockFactory());
    }


    public void makeSpellCheckerIndex(String field) throws IOException {
        IndexReader wcDirectory = IndexReader.open(indexDirectory);
        LuceneDictionary dict = new LuceneDictionary(wcDirectory,field);
        SpellChecker spellchecker = new SpellChecker(spellCheckerDir);
        spellchecker.indexDictionary(dict, new IndexWriterConfig(Version.LUCENE_36,new KeywordAnalyzer()), false);
        wcDirectory.close();

    }

    private Document getDocument(String content, String fileName, String filePath) throws IOException{
        Document document = new Document();
        //index file contents
        Field contentField = new Field("content", content , Store.YES,Index.ANALYZED);
        //index file name
        Field fileNameField = new Field("fileName", fileName, Store.YES, Index.NOT_ANALYZED);
        //index file path
        Field filePathField = new Field("fileName", filePath, Store.YES, Index.NOT_ANALYZED);
        document.add(contentField);
        document.add(fileNameField);
        document.add(filePathField);

        return document;
    }

    public void indexSimpleFile(File file) throws IOException{
        System.out.println("Indexing "+file.getCanonicalPath());
        Scanner sc = new Scanner(file).useDelimiter("\n");
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36,mainAnalyzer);
        IndexWriter writer = new IndexWriter(indexDirectory,config);
        while (sc.hasNext()){
           Document document = getDocument(sc.next(),file.getName(),file.getAbsolutePath());
            writer.addDocument(document);
        }
        writer.close();
    }

    public void indexCSVFile(File file, LuceneSchema schema) throws IOException {
        System.out.println("Indexing "+file.getCanonicalPath());
        Parser parser = new CSVDocumentParser();
        List<Document> documentsToIndex = parser.parse(new FileInputStream(file),schema);
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36,getPerFieldAnalyzerWrapper(schema));
        IndexWriter writer = new IndexWriter(indexDirectory,config);
        for(Document doc : documentsToIndex){
            writer.addDocument(doc);
        }
        writer.close();
    }


    public void clearIndex() throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36,mainAnalyzer);
        IndexWriter writer = new IndexWriter(indexDirectory,config);
        writer.deleteAll();
        writer.close();
        IndexWriterConfig config2 = new IndexWriterConfig(Version.LUCENE_36,new KeywordAnalyzer());
        IndexWriter writer2 = new IndexWriter(spellCheckerDir,config2);
        writer2.deleteAll();
        writer2.close();
    }

    private PerFieldAnalyzerWrapper getPerFieldAnalyzerWrapper(LuceneSchema schema){
        Map<String,Analyzer> analyzerPerField = new HashMap<String,Analyzer>();
        for(LuceneFieldDefinition def : schema.getFieldDefinitions()){
           analyzerPerField.put(def.getFieldName(),def.getFieldAnalyzer());
        }
        return new PerFieldAnalyzerWrapper(new StandardAnalyzer(Version.LUCENE_36),analyzerPerField);
    }
}
