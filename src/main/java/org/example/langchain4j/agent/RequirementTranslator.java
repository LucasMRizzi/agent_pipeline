package org.example.langchain4j.agent;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;

public class RequirementTranslator implements Agent<String, String> {

    private final RequirementTranslatorAgent aiService;

    public RequirementTranslator(ChatModel model) {
        this.aiService = AiServices.builder(RequirementTranslatorAgent.class)
                .chatModel(model)
                .build();
    }

    @Override
    public String execute(String textoEmPortugues) {
        try {
            return aiService.rewrite(textoEmPortugues);
        } catch (Exception e) {
            System.err.printf("Erro na reescrita: %s%n", e.getMessage());
            return textoEmPortugues;
        }
    }
}
