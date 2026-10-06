package org.example.langchain4j.agent;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;

public class RequirementsRewriter implements Agent<RequirementsRewriter.Input, String> {

    public record Input(TextSegment originalChunk, String analysis) {}

    private final RequirementsRewriterAgent aiService;

    public RequirementsRewriter(ChatModel model, EmbeddingStoreContentRetriever contentRetriever) {
        this.aiService = AiServices.builder(RequirementsRewriterAgent.class)
                .chatModel(model)
                .contentRetriever(contentRetriever)
                .build();
    }
    public RequirementsRewriter(ChatModel model) {
        this.aiService = AiServices.builder(RequirementsRewriterAgent.class)
                .chatModel(model)
                .build();
    }

    @Override
    public String execute(Input input) {
        try {
            return aiService.rewrite(input.originalChunk().text(), input.analysis());
        } catch (Exception e) {
            System.err.printf("Erro na reescrita do chunk: %s%n", e.getMessage());
            return input.originalChunk().text();
        }
    }
}