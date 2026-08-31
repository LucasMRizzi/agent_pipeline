package org.example.langchain4j;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.util.ArrayList;
import java.util.List;


public class AgentRunner {

    public static void main(String[] args) {

        String apiKey = System.getenv("NVIDIA_API_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            throw new RuntimeException("NVIDIA_API_KEY não foi encontrada.");
        }

        OpenAiChatModel model = OpenAiChatModel.builder()
                .baseUrl("https://integrate.api.nvidia.com/v1")
                .apiKey(apiKey)
                .modelName("meta/llama-3.2-11b-vision-instruct")
                .build();

        DocumentHandler documentHandler = new DocumentHandler();

        Document documento = documentHandler.loadDocument("requisitos.pdf");
        System.out.println("Documento processado.");
        List<TextSegment> chunks = documentHandler.documentChunking(documento);
        System.out.println("Documento dividido em chunks.");


        // Agente 1 - Análise de requisitos

        List<String> analises = new ArrayList<>();
        System.out.println("\nIniciando etapa 1: Análise de requisitos\n");

        for (int i = 0; i < chunks.size(); i++) {
            TextSegment chunk = chunks.get(i);

            String promptAnalise = """
                Você é um engenheiro de requisitos.

                Analise o trecho abaixo.

                Identifique:
                - ambiguidades;
                - incompletudes;
                - inconsistências;
                - problemas de verificabilidade.

                Para cada problema, proponha uma correção.

                Retorne somente o resultado da análise.

                TRECHO:
                %s
                """.formatted(chunk.text());

            try {
                String analise = model.chat(promptAnalise);
                analises.add(analise);
                System.out.printf(">>> Chunk %d/%d analisado com sucesso.%n", (i + 1), chunks.size());
            } catch (Exception e) {
                System.err.printf("Erro ao analisar chunk %d: %s%n", (i + 1), e.getMessage());
                analises.add("Nenhuma alteração proposta (falha na análise do modelo).");
            }
        }

        if (analises.size() != chunks.size()) {
            throw new IllegalStateException(
                    String.format("Divergência: %d chunks para %d análises.", chunks.size(), analises.size())
            );
        }

        System.out.println(analises);

        // Agente 2 - Correção requisitos

        List<String> chunksCorrigidos = new ArrayList<>();
        System.out.println("\nIniciando Etapa 2: Atualização do Texto\n");

        for (int i = 0; i < chunks.size(); i++) {
            String chunkOriginal = chunks.get(i).text();
            String analiseCorrespondente = analises.get(i);

            String promptReescrita = """
                Você é um especialista em documentação de software.
                Sua tarefa é reescrever o TRECHO ORIGINAL de requisitos aplicando as correções sugeridas na ANÁLISE.
                
                Regras:
                - Mantenha um tom técnico, formal e preciso (padrão IEEE/Engenharia de Requisitos).
                - Retorne APENAS o texto do requisito corrigido, sem explicações ou introduções.
                
                TRECHO ORIGINAL:
                %s
                
                ANÁLISE E CORREÇÕES SUGERIDAS:
                %s
                """.formatted(chunkOriginal, analiseCorrespondente);

            try {
                String chunkCorrigido = model.chat(promptReescrita);
                chunksCorrigidos.add(chunkCorrigido);
                System.out.printf(">>> Chunk %d/%d reescrito com sucesso.%n", (i + 1), chunks.size());
            } catch (Exception e) {
                System.err.printf("Erro ao reescrever chunk %d: %s%n", (i + 1), e.getMessage());
                chunksCorrigidos.add(chunkOriginal);
            }
        }

        String documentoCompletoAtualizado = String.join("\n\n", chunksCorrigidos);

        System.out.println("\n================ REQUISITOS ATUALIZADOS ================\n");
        System.out.println(documentoCompletoAtualizado);

        // Agente 3 - Diagrama de classes

        System.out.println("\nIniciando Etapa 3: Geração do Diagrama PlantUML\n");

        String promptUML = """
            Você é um arquiteto de software.
            Com base na especificação completa de requisitos abaixo, gere o código de um Diagrama de Classes UML em formato PlantUML (@startuml ... @enduml).
            
            Diretrizes:
            - Identifique as entidades principais, seus atributos (com tipos) e métodos principais.
            - Defina os relacionamentos (associação, agregação, composição, herança) e multiplicidades.
            - Retorne SOMENTE o bloco de código PlantUML.
            
            DOCUMENTO DE REQUISITOS COMPLETO:
            %s
            """.formatted(documentoCompletoAtualizado);

        String codigoPlantUML = model.chat(promptUML);

        System.out.println("\n================ CÓDIGO PLANTUML ================\n");
        System.out.println(codigoPlantUML);
    }

}
