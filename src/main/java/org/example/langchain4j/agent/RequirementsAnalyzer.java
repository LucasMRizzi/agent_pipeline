package org.example.langchain4j.agent;

import com.healthmarketscience.jackcess.util.OleBlob;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;

public class RequirementsAnalyzer implements Agent<TextSegment, String> {

    private final RequirementsAnalyzerAgent aiService;

    public RequirementsAnalyzer(ChatModel model, EmbeddingStoreContentRetriever contentRetriever) {
        this.aiService = AiServices.builder(RequirementsAnalyzerAgent.class)
                .chatModel(model)
                .contentRetriever(contentRetriever)
                .build();
    }
    public RequirementsAnalyzer(ChatModel model) {
        this.aiService = AiServices.builder(RequirementsAnalyzerAgent.class)
                .chatModel(model)
                .build();
    }

    @Override
    public String execute(TextSegment chunk) {
        try {
            return aiService.analyze(chunk.text());
        } catch (Exception e) {
            System.err.printf("Erro na análise do chunk: %s%n", e.getMessage());
            return "Nenhuma alteração proposta (falha na análise do modelo).";
        }
    }
}