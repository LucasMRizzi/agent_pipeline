package org.example.langchain4j.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public record Conditions(
        String textModel,
        String codeModel,
        String embeddingModel,
        Integer chunkSize,
        Integer ragMaxResults,
        String ragDescription
) {
}