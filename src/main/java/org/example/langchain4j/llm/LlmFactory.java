package org.example.langchain4j.llm;

import dev.langchain4j.model.chat.ChatModel;

public class LlmFactory {
    public static ChatModel create(LLM provider) {

        return switch (provider) {

            case LLAMA -> LlamaLlm.create();

            case GPT -> GPTLlm.create();

        };
    }
}
