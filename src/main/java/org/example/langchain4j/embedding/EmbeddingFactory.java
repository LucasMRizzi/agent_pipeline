package org.example.langchain4j.embedding;

import dev.langchain4j.model.embedding.EmbeddingModel;

public class EmbeddingFactory {
    public static EmbeddingModel create(Embedding provider) {

        return switch (provider) {

            case NEMOTRON -> NemotronEmbedding.create();

        };
    }
}
