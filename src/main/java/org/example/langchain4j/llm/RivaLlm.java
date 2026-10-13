package org.example.langchain4j.llm;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;


public class RivaLlm {

    public static ChatModel create() {

        String nvidiaKey = System.getenv("NVIDIA_API_KEY");

        return OpenAiChatModel.builder()
                .baseUrl("https://integrate.api.nvidia.com/v1")
                .apiKey(nvidiaKey)
                .modelName("nvidia/riva-translate-4b-instruct-v2")
                .temperature(0.0)
                .build();
    }
}
