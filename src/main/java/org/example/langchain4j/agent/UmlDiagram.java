package org.example.langchain4j.agent;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;

public class UmlDiagram implements Agent<UmlDiagram.Input, String> {

    public record Input(String fullDocument, String patterns) {}

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
    public String execute(Input input) {
        return aiService.generatePlantUml(input.fullDocument, input.patterns);
    }
}