package org.example.langchain4j;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.loader.UrlDocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.example.langchain4j.agent.*;
import org.example.langchain4j.audit.Conditions;
import org.example.langchain4j.document.DocumentLoader;
import org.example.langchain4j.document.PdfDocumentWriter;
import org.example.langchain4j.audit.PipelineAudit;
import org.example.langchain4j.embedding.Embedding;
import org.example.langchain4j.embedding.EmbeddingFactory;
import org.example.langchain4j.llm.LLM;
import org.example.langchain4j.llm.LlmFactory;
import org.example.langchain4j.pipeline.PipelineResult;
import org.example.langchain4j.pipeline.RequirementPipeline;
import org.example.langchain4j.rag.RAG;
import org.example.langchain4j.rag.RAGFactory;
import org.example.langchain4j.rag.RAGFactoryGeneric;
import org.example.langchain4j.rag.RAGFactoryRequirements;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;


public class AgentRunner {

    public static void main(String[] args) throws IOException {

        // ================================================
        // Iniciando Ferramentas
        // ================================================

        // -> Carrega Documentos
        DocumentLoader documentLoader = new DocumentLoader();

        // -> Scanner
        Scanner scanner = new Scanner(System.in);

        // ================================================
        // Escolhendo Modelos
        // ================================================

        // -> Modelo de Embedding
        EmbeddingModel embeddingModel = escolherEmbeddingModel(scanner);

        // -> LLMs
        System.out.println("\nEscolha o modelo de linguagem para textos:");
        ChatModel textModel = escolherLlm(scanner);

        System.out.println("\nEscolha o modelo de linguagem para códigos:");
        ChatModel codeModel = escolherLlm(scanner);

        // ================================================
        // Iniciando RAG
        // ================================================

        // -> Carregando documentos da RAG
        List<Document> documentos = documentLoader.loadDocuments("./diagramas");
        List<Document> documentosReq = documentLoader.loadDocuments("./requisitos");

        // -> Criando Banco de Vetores
        RAG reqRag = new RAGFactoryRequirements(500).create(embeddingModel);
        RAG umlRag = new RAGFactoryGeneric().create(embeddingModel);

        // -> Ingestão dos documentos
        umlRag.ingestor().ingest(documentos);
        reqRag.ingestor().ingest(documentosReq);

        // ================================================
        // Iniciando Agentes
        // ================================================

        // -> Agente de Análise de Requisitos
        RequirementsAnalyzer analyzerAgent = new RequirementsAnalyzer(textModel, reqRag.retriever());

        // -> Agente de Consolidação de Análise de Requisitos
        AnalysisConsolidator consolidatorAgent = new AnalysisConsolidator(textModel);

        // -> Agente de Reescrita de Requisitos
        RequirementsRewriter rewriterAgent = new RequirementsRewriter(textModel, reqRag.retriever());

        // -> Agente de Criação de Diagrama UML
        UmlDiagram umlAgent = new UmlDiagram(codeModel, umlRag.retriever());

        // ================================================
        // Carregando Documento de Requisitos
        // ================================================

        Document documento = documentLoader.loadDocument("requisitos.pdf");

        // ================================================
        // Iniciando Pipeline
        // ================================================

        // -> Cria o pipeline e salva os resultados
        PipelineResult pipelineResult = new RequirementPipeline(analyzerAgent, consolidatorAgent, rewriterAgent, umlAgent).execute(documento.toTextSegment());

        // ================================================
        // Gerando Auditáveis
        // ================================================

        // -> Salva as condições do pipeline
        Conditions conditions = new Conditions(
                textModel.defaultRequestParameters().modelName(),
                codeModel.defaultRequestParameters().modelName(),
                embeddingModel.modelName(),
                1000,
                3,
                "Rag utilizado nos agentes de analise, reescrita e uml"
        );

        // -> Salva os arquivos auditáveis de resultados
        PipelineAudit pipelineAudit = new PipelineAudit("./audits");
        pipelineAudit.save(conditions, pipelineResult);
    }

    private static EmbeddingModel escolherEmbeddingModel(Scanner scanner) {
        Embedding[] modelos = Embedding.values();

        if (modelos.length == 0) {
            throw new IllegalStateException("Nenhum modelo disponível.");
        }

        while (true) {
            System.out.println("\nEscolha o modelo de embedding:");

            for (int i = 0; i < modelos.length; i++) {
                System.out.printf("%d - %s%n", i + 1, modelos[i].name());
            }

            System.out.print("Digite a opção: ");

            try {
                int opcao = Integer.parseInt(scanner.nextLine().trim());

                if (opcao >= 1 && opcao <= modelos.length) {
                    Embedding escolhido = modelos[opcao - 1];

                    System.out.println("Modelo selecionado: " + escolhido);
                    return EmbeddingFactory.create(escolhido);
                }
            } catch (NumberFormatException e) {
                // A mensagem abaixo também cobre entradas não numéricas.
            }

            System.out.println("Opção inválida. Escolha um número do menu.");
        }
    }

    private static ChatModel escolherLlm(Scanner scanner) {
        LLM[] modelos = LLM.values();

        if (modelos.length == 0) {
            throw new IllegalStateException("Nenhum modelo disponível.");
        }

        while (true) {

            for (int i = 0; i < modelos.length; i++) {
                System.out.printf("%d - %s%n", i + 1, modelos[i].name());
            }

            System.out.print("Digite a opção: ");

            try {
                int opcao = Integer.parseInt(scanner.nextLine().trim());

                if (opcao >= 1 && opcao <= modelos.length) {
                    LLM escolhido = modelos[opcao - 1];

                    System.out.println("Modelo selecionado: " + escolhido);
                    return LlmFactory.create(escolhido);
                }
            } catch (NumberFormatException e) {
                // A mensagem abaixo também cobre entradas não numéricas.
            }

            System.out.println("Opção inválida. Escolha um número do menu.");
        }
    }
}
