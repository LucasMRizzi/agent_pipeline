package org.example.langchain4j.embedding;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;

public class NemotronEmbedding {

    public static EmbeddingModel create() {

        String nvidiaKey = System.getenv("NVIDIA_API_KEY");

        return OpenAiEmbeddingModel.builder()
                .baseUrl("https://integrate.api.nvidia.com/v1")
                .apiKey(nvidiaKey)
                .modelName("nvidia/nemotron-3-embed-1b")
                .build();
    }
}