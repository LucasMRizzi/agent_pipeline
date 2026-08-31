package org.example.langchain4j;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.segment.TextSegment;

import java.util.List;

public class DocumentHandler {

    public DocumentHandler(){
    }

    public Document loadDocument(String path){
        return FileSystemDocumentLoader.loadDocument(path,
            new ApachePdfBoxDocumentParser()
        );
    }

    public List<TextSegment> documentChunking(Document document){
        DocumentByParagraphSplitter splitter = new DocumentByParagraphSplitter(1000, 20);

        return splitter.split(document);
    }
}
