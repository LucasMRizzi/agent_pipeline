package org.example.langchain4j.rag;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class PlantUcdDocumentLoader {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Document> loadTrainDataset(String filePath) throws IOException {
        try (Stream<String> lines = Files.lines(
                Path.of(filePath),
                StandardCharsets.UTF_8
        )) {
            return lines
                    .filter(line -> !line.isBlank())
                    .map(this::toDocument)
                    .toList();
        }
    }

    private Document toDocument(String jsonLine) {
        try {
            PlantUcdRecord record = objectMapper.readValue(
                    jsonLine,
                    PlantUcdRecord.class
            );

            String content = """
                    REQUIREMENT PATTERN:
                    %s

                    REFERENCE PLANTUML:
                    %s
                    """.formatted(
                    record.humanLang(),
                    record.plantUml()
            );

            Metadata metadata = new Metadata();
            metadata.put("source", "PlantUCD");
            metadata.put("model", record.model());
            metadata.put("requirementIndex", record.requirementIndex());
            metadata.put("type", "uml-pattern");
            metadata.put("language", "en");

            return Document.from(content, metadata);

        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Erro ao converter registro do PlantUCD",
                    e
            );
        }
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PlantUcdRecord(
            @JsonProperty("HumanLang")
            String humanLang,

            @JsonProperty("PlantUML")
            String plantUml,

            @JsonProperty("Model")
            String model,

            @JsonProperty("RequirementIndex")
            String requirementIndex
    ) {
    }
}