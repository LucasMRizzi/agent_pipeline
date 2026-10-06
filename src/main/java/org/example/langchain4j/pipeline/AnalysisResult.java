package org.example.langchain4j.pipeline;

public record AnalysisResult(
        int chunkIndex,
        String chunkText,
        String analysis
) {
}