package org.example.langchain4j.pipeline;

import dev.langchain4j.data.segment.TextSegment;
import org.example.langchain4j.agent.AnalysisConsolidator;
import org.example.langchain4j.agent.RequirementsAnalyzer;
import org.example.langchain4j.agent.RequirementsRewriter;
import org.example.langchain4j.agent.UmlDiagram;
import org.example.langchain4j.chunk.Chunker;
import org.example.langchain4j.chunk.Chunk;
import org.example.langchain4j.chunk.RequirementChunker;

import java.util.ArrayList;
import java.util.List;

public class RequirementPipeline {

    private final Chunker chunker = new RequirementChunker(1000);

    private final RequirementsAnalyzer analyzerAgent;
    private final AnalysisConsolidator consolidatorAgent;
    private final RequirementsRewriter rewriterAgent;
    private final UmlDiagram diagramAgent;

    public RequirementPipeline(
            RequirementsAnalyzer analyzerAgent,
            AnalysisConsolidator consolidatorAgent,
            RequirementsRewriter rewriterAgent,
            UmlDiagram diagramAgent) {

        this.analyzerAgent = analyzerAgent;
        this.consolidatorAgent = consolidatorAgent;
        this.rewriterAgent = rewriterAgent;
        this.diagramAgent = diagramAgent;
    }

    public PipelineResult execute(TextSegment requirements) {

        List<Chunk> chunks = chunker.split(requirements.text());

        // Agente 1
        System.out.println("\n\n-------------------------------------------");
        System.out.println("Etapa 1 -> Análise de Requisitos");
        System.out.println("-------------------------------------------\n");

        List<AnalysisResult> analyses = new ArrayList<>();

        for (Chunk chunk : chunks) {

            AnalysisResult result = new AnalysisResult(
                    chunk.index(),
                    chunk.segment().text(),
                    analyzerAgent.execute(chunk.segment())
            );

            System.out.println("Analisado Chunk " + (chunk.index() + 1) + " de " + chunks.size());

            analyses.add(result);
        }

        System.out.println("\n\n-------------------------------------------");
        System.out.println("Etapa 2 -> Consolidação da análise");
        System.out.println("-------------------------------------------\n\n");
        String consolidatedAnalysis = consolidatorAgent.execute(analyses);

        // Agente 2
        System.out.println("\n\n-------------------------------------------");
        System.out.println("Etapa 3 -> Reescrita de Requisitos");
        System.out.println("-------------------------------------------\n\n");

        RequirementsRewriter.Input input = new RequirementsRewriter.Input(requirements, consolidatedAnalysis);

        String rewritten = rewriterAgent.execute(input);

        // Agente 3
        System.out.println("\n\n-------------------------------------------");
        System.out.println("Etapa 4 -> Criação do Diagrama");
        System.out.println("-------------------------------------------\n\n");

        String diagram = diagramAgent.execute(rewritten);

        return new PipelineResult(
                requirements,
                analyses,
                consolidatedAnalysis,
                rewritten,
                diagram
        );
    }
}
