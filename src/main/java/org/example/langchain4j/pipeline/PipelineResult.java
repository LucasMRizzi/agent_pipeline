package org.example.langchain4j.pipeline;

import dev.langchain4j.data.segment.TextSegment;

import java.util.List;

public record PipelineResult(
        TextSegment requirements,
        List<AnalysisResult> analyses,
        String consolidatedAnalysis,
        String rewritten,
        String diagram
)
{

}
