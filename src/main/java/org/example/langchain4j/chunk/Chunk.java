package org.example.langchain4j.chunk;

import dev.langchain4j.data.segment.TextSegment;

public record Chunk(
        int index,
        TextSegment segment
) {

    public String text() {
        return segment.text();
    }
}
