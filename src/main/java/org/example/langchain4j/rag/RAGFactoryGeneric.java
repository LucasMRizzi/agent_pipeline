package org.example.langchain4j.rag;

import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

public class RAGFactoryGeneric implements RAGFactory {

    @Override
    public RAG create(EmbeddingModel embeddingModel) {

        InMemoryEmbeddingStore<TextSegment> embeddingStore =
                new InMemoryEmbeddingStore<>();

        DocumentSplitter splitter =
                DocumentSplitters.recursive(500, 50);

        EmbeddingStoreIngestor ingestor =
                EmbeddingStoreIngestor.builder()
                        .documentSplitter(splitter)
                        .embeddingModel(embeddingModel)
                        .embeddingStore(embeddingStore)
                        .build();

        EmbeddingStoreContentRetriever retriever =
                EmbeddingStoreContentRetriever.builder()
                        .embeddingStore(embeddingStore)
                        .embeddingModel(embeddingModel)
                        .maxResults(3)
                        .build();

        return new RAG(
                embeddingStore,
                ingestor,
                retriever
        );
    }
}
