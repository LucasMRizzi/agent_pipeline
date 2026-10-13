package org.example.langchain4j.llm;

import dev.langchain4j.model.chat.ChatModel;

public class TranslateLlmFactory {

    public static ChatModel create(TranslateLLM provider) {

        return switch (provider) {

            case RIVA -> RivaLlm.create();

        };
    }
}
