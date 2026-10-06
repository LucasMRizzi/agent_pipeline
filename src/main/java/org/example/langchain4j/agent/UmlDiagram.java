package org.example.langchain4j.agent;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

public class UmlDiagram implements Agent<String, String> {

    private final UmlDiagramAgent aiService;

    public UmlDiagram(ChatModel model, EmbeddingStoreContentRetriever contentRetriever) {

        this.aiService = AiServices.builder(UmlDiagramAgent.class)
                .chatModel(model)
                .contentRetriever(contentRetriever)
                .build();
    }

    public UmlDiagram(ChatModel model) {

        this.aiService = AiServices.builder(UmlDiagramAgent.class)
                .chatModel(model)
                .build();
    }

    @Override
    public String execute(String fullDocument) {
        return aiService.generatePlantUml(fullDocument);
    }
}