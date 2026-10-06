package org.example.langchain4j.llm;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.time.Duration;

public class GPTLlm {

    public static ChatModel create() {

        String nvidiaKey = System.getenv("NVIDIA_API_KEY");

        return OpenAiChatModel.builder()
                .baseUrl("https://integrate.api.nvidia.com/v1")
                .apiKey(nvidiaKey)
                .modelName("openai/gpt-oss-20b")
                .timeout(Duration.ofMinutes(5))
                .build();
    }
}
