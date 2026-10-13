package org.example.langchain4j.pipeline;

import dev.langchain4j.data.segment.TextSegment;
import org.example.langchain4j.agent.*;
import org.example.langchain4j.chunk.Chunker;
import org.example.langchain4j.chunk.Chunk;
import org.example.langchain4j.chunk.RequirementChunker;
import org.example.langchain4j.rag.PlantUcdPatternRetriever;

import java.util.ArrayList;
import java.util.List;

public class RequirementPipeline {

    private final Chunker chunker = new RequirementChunker(1000);

    private final RequirementsAnalyzer analyzerAgent;
    private final AnalysisConsolidator consolidatorAgent;
    private final RequirementsRewriter rewriterAgent;
    private final RequirementTranslator translatorAgent;
    private final UmlDiagram diagramAgent;

    private final PlantUcdPatternRetriever plantUcdPatternRetriever;

    public RequirementPipeline(
            RequirementsAnalyzer analyzerAgent,
            AnalysisConsolidator consolidatorAgent,
            RequirementsRewriter rewriterAgent,
            RequirementTranslator translatorAgent,
            UmlDiagram diagramAgent,
            PlantUcdPatternRetriever plantUcdPatternRetriever) {

        this.analyzerAgent = analyzerAgent;
        this.consolidatorAgent = consolidatorAgent;
        this.rewriterAgent = rewriterAgent;
        this.translatorAgent = translatorAgent;
        this.diagramAgent = diagramAgent;
        this.plantUcdPatternRetriever = plantUcdPatternRetriever;
    }

    public PipelineResult execute(TextSegment requirements) {

        List<Chunk> chunks = chunker.split(requirements.text());

        /**
         * Agente 01 - Análise de Requisitos
         *
         * Input -> Documento de Requisitos original
         *
         * Process -> Chunking do documento original e análises individuais por conjunto de requisitos
         *
         * Output -> Lista de análises dos problemas identificados
         * */
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

        /**
         * Agente 02 - Consolidação da análise
         *
         * Input -> Lista de análises dos problemas identificados
         *
         * Output -> String com a consolidação de todas as análises
         *
         * ~ Evita análises duplicadas para o mesmo trecho de requisitos
         * */
        System.out.println("\n\n-------------------------------------------");
        System.out.println("Etapa 2 -> Consolidação da análise");
        System.out.println("-------------------------------------------\n\n");
        String consolidatedAnalysis = consolidatorAgent.execute(analyses);

        /**
         * Agente 03 - Reescrita de requisitos
         *
         * Input -> Documento de requisitos original
         *       -> Lista de análises dos problemas identificados
         *
         * Output -> String com a reescrita do documento
         *
         * */
        System.out.println("\n\n-------------------------------------------");
        System.out.println("Etapa 3 -> Reescrita de Requisitos");
        System.out.println("-------------------------------------------\n\n");

        RequirementsRewriter.Input input = new RequirementsRewriter.Input(requirements, consolidatedAnalysis);

        String rewritten = rewriterAgent.execute(input);

        /**
         * Agente 04 - Tradução dos requisitos (Caso necessário)
         *
         * Input -> Documento de requisitos reescrito
         *
         * Output -> String com a reescrita do documento em inglês
         *
         * */
        System.out.println("\n\n-------------------------------------------");
        System.out.println("Etapa 4 -> Tradução dos Requisitos");
        System.out.println("-------------------------------------------\n\n");

        String rewrittenEnglish = translatorAgent.execute(rewritten);

        // Agente 3
        System.out.println("\n\n-------------------------------------------");
        System.out.println("Etapa 5 -> Recuperação de Padrões UML");
        System.out.println("-------------------------------------------\n\n");

        List<Chunk> chunksInEnglish = chunker.split(rewrittenEnglish);

        String patterns = plantUcdPatternRetriever.retrievePatterns(chunksInEnglish);

        /**
         * Agente 05 - Geração do diagrama de classes UML
         *
         * Input -> Documento de requisitos reescrito em inglês
         *       -> Padrões identificados (plantUCD)
         *
         * Output -> Código do diagrama PUML
         *
         * */
        System.out.println("\n\n-------------------------------------------");
        System.out.println("Etapa 6 -> Geração do Diagrama PUML");
        System.out.println("-------------------------------------------\n\n");

        UmlDiagram.Input inputUml = new UmlDiagram.Input(rewrittenEnglish, patterns);

        String diagram = diagramAgent.execute(inputUml);

        return new PipelineResult(
                requirements,
                analyses,
                consolidatedAnalysis,
                rewritten,
                rewrittenEnglish,
                patterns,
                diagram
        );
    }
}
