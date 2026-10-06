package org.example.langchain4j.rag;

import dev.langchain4j.model.embedding.EmbeddingModel;

public interface RAGFactory {

    RAG create(EmbeddingModel embeddingModel);
}
