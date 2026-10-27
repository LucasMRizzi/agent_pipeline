package org.example.langchain4j.chunk;

import dev.langchain4j.data.segment.TextSegment;

import java.util.ArrayList;
import java.util.List;

public class RequirementChunker implements Chunker {

    private final int maxCharacters;

    public RequirementChunker(int maxCharacters) {
        if (maxCharacters <= 0) {
            throw new IllegalArgumentException(
                    "O tamanho máximo do chunk deve ser maior que zero."
            );
        }

        this.maxCharacters = maxCharacters;
    }

    @Override
    public List<Chunk> split(String text) {

        if (text == null || text.isBlank()) {
            return List.of();
        }

        List<Chunk> chunks = new ArrayList<>();

        String[] requirements = text.split(
                "(?m)(?=^[\\t ]*(?:(?:RF|RNF|REQ|Requisito)[\\t ]*[-:]?[\\t ]*\\d+|\\d+\\.[\\t ]+))"
        );

        StringBuilder currentChunk = new StringBuilder();

        for (String requirement : requirements) {

            requirement = requirement.trim();

            if (requirement.isEmpty()) {
                continue;
            }

            if (currentChunk.isEmpty()) {
                currentChunk.append(requirement);
                continue;
            }

            int newSize =
                    currentChunk.length()
                            + 2
                            + requirement.length();

            if (newSize <= maxCharacters) {

                currentChunk
                        .append("\n\n")
                        .append(requirement);

            } else {

                chunks.add(createChunk(
                        chunks.size(),
                        currentChunk.toString()
                ));

                currentChunk.setLength(0);
                currentChunk.append(requirement);
            }
        }

        if (!currentChunk.isEmpty()) {

            chunks.add(createChunk(
                    chunks.size(),
                    currentChunk.toString()
            ));
        }

        return chunks;
    }

    private Chunk createChunk(int index, String text) {

        TextSegment segment =
                TextSegment.from(text);

        return new Chunk(index, segment);
    }
}