package org.example.langchain4j.rag;

import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.util.List;

public class RagFactoryPlantUCD implements RAGFactory {

    @Override
    public RAG create(EmbeddingModel embeddingModel) {

        EmbeddingStore<TextSegment> embeddingStore =
                new InMemoryEmbeddingStore<>();

        DocumentSplitter noSplit = document ->
                List.of(document.toTextSegment());

        EmbeddingStoreIngestor ingestor =
                EmbeddingStoreIngestor.builder()
                        .documentSplitter(noSplit)
                        .embeddingModel(embeddingModel)
                        .embeddingStore(embeddingStore)
                        .build();

        EmbeddingStoreContentRetriever contentRetriever =
                EmbeddingStoreContentRetriever.builder()
                        .embeddingModel(embeddingModel)
                        .embeddingStore(embeddingStore)
                        .maxResults(2)
                        .minScore(0.60)
                        .build();

        return new RAG(embeddingStore, ingestor, contentRetriever);
    }
}