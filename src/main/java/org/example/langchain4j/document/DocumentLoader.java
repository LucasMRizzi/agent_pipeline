package org.example.langchain4j.document;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;

import java.util.List;

public class DocumentLoader {

    public List<Document> loadDocuments(String path) {
        return FileSystemDocumentLoader.loadDocuments(
                path,
                new ApacheTikaDocumentParser()
        );
    }

    public Document loadDocument(String path) {
        return FileSystemDocumentLoader.loadDocument(
                path,
                new ApacheTikaDocumentParser()
        );
    }
}
