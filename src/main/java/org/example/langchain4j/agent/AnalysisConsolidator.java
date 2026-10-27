package org.example.langchain4j.agent;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import org.example.langchain4j.pipeline.AnalysisResult;
import dev.langchain4j.service.AiServices;

import java.util.List;
import java.util.stream.Collectors;

public class AnalysisConsolidator implements Agent<List<AnalysisResult>, String> {

    private final AnalysisConsolidatorAgent aiService;

    public AnalysisConsolidator(ChatModel model, EmbeddingStoreContentRetriever contentRetriever) {
        this.aiService = AiServices.builder(AnalysisConsolidatorAgent.class)
                .chatModel(model)
                .contentRetriever(contentRetriever)
                .build();
    }

    public AnalysisConsolidator(ChatModel model) {
        this.aiService = AiServices.builder(AnalysisConsolidatorAgent.class)
                .chatModel(model)
                .build();
    }

    @Override
    public String execute(List<AnalysisResult> analyses) {
        try {
            String analysesText = analyses.stream()
                    .map(this::formatAnalysis)
                    .collect(Collectors.joining("\n\n"));

            return aiService.consolidate(analysesText);
        } catch (Exception e) {
            System.err.printf("Erro na análise do chunk: %s%n", e.getMessage());
            return "Nenhuma alteração proposta (falha na análise do modelo).";
        }
    }


    private String formatAnalysis(AnalysisResult result) {

        return """
                === Chunk %d ===

                Requisitos:
                %s

                Análise:
                %s
                """.formatted(
                result.chunkIndex(),
                result.chunkText(),
                result.analysis()
        );
    }
}