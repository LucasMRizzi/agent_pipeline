package org.example.langchain4j.rag;

import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import org.example.langchain4j.chunk.Chunk;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PlantUcdPatternRetriever {

    private final EmbeddingStoreContentRetriever contentRetriever;

    public PlantUcdPatternRetriever(EmbeddingStoreContentRetriever contentRetriever) {
        this.contentRetriever = contentRetriever;
    }

    public String retrievePatterns(List<Chunk> requirementsInEnglish) {
        Set<String> patterns = new LinkedHashSet<>();

        for (Chunk chunk : requirementsInEnglish) {

            List<Content> contents = contentRetriever.retrieve(Query.from(chunk.segment().text()));

            for (Content content : contents) {
                patterns.add(content.textSegment().text());
            }
        }

        return String.join("\n\n---\n\n", patterns);
    }
}
