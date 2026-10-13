package org.example.langchain4j.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.langchain4j.document.PdfDocumentWriter;
import org.example.langchain4j.pipeline.AnalysisResult;
import org.example.langchain4j.pipeline.PipelineResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class PipelineAudit {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static final PdfDocumentWriter pdfDocumentWriter = new PdfDocumentWriter();
    private final Path baseDirectory;
    private final ObjectMapper objectMapper;

    public PipelineAudit(String baseDirectory) {

        this.baseDirectory = Path.of(baseDirectory);

        this.objectMapper = new ObjectMapper();

        this.objectMapper.enable(
                SerializationFeature.INDENT_OUTPUT
        );
    }

    public Path save(
            Conditions conditions,
            PipelineResult pipelineResult
    ) throws IOException {



        String timestamp =
                LocalDateTime.now().format(FORMATTER);

        Path testDirectory =
                baseDirectory.resolve(timestamp);

        Files.createDirectories(testDirectory);

        saveConditions(
                testDirectory,
                conditions
        );

        write(
                testDirectory.resolve("input.txt"),
                pipelineResult.requirements().toString()
        );

        writeAnalyses(
                testDirectory.resolve("analysis.txt"),
                pipelineResult.analyses()
        );

        write(
                testDirectory.resolve("consolidated-analysis.txt"),
                pipelineResult.consolidatedAnalysis()
        );

        write(
                testDirectory.resolve("rewritten-requirements.txt"),
                pipelineResult.rewritten()
        );

        write(
                testDirectory.resolve("diagram.puml"),
                pipelineResult.diagram()
        );

        write(
                testDirectory.resolve("rewritten-english.txt"),
                pipelineResult.rewrittenEnglish()
        );

        write(
                testDirectory.resolve("patterns.txt"),
                pipelineResult.identifiedPatterns()
        );

        pdfDocumentWriter.write(
                pipelineResult.rewritten(),
                testDirectory.resolve("documento_modificado.pdf")
        );

        return testDirectory;
    }

    private void saveConditions(
            Path directory,
            Conditions conditions
    ) throws IOException {

        objectMapper.writeValue(
                directory
                        .resolve("conditions.json")
                        .toFile(),
                conditions
        );
    }

    private void write(
            Path path,
            String content
    ) throws IOException {

        Files.writeString(
                path,
                content == null ? "" : content,
                StandardCharsets.UTF_8
        );
    }

    private void writeAnalyses(
            Path path,
            List<AnalysisResult> analyses
    ) throws IOException {

        String content =
                analyses.stream()
                        .map(result -> """
                            ========================================
                            CHUNK %d
                            ========================================

                            %s

                            ANÁLISE:
                            %s

                            """.formatted(
                                result.chunkIndex(),
                                result.chunkText(),
                                result.analysis()
                        ))
                        .collect(Collectors.joining("\n"));

        write(path, content);
    }

}