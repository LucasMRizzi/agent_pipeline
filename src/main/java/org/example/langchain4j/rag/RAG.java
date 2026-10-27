package org.example.langchain4j.rag;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;

public record RAG(
        EmbeddingStore<TextSegment> embeddingStore,
        EmbeddingStoreIngestor ingestor,
        EmbeddingStoreContentRetriever retriever
) {

}