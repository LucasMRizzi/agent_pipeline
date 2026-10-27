package org.example.langchain4j.llm;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class LlamaLlm {

    public static ChatModel create() {

        String nvidiaKey = System.getenv("NVIDIA_API_KEY");

        return OpenAiChatModel.builder()
                .baseUrl("https://integrate.api.nvidia.com/v1")
                .apiKey(nvidiaKey)
                .modelName("meta/llama-3.2-11b-vision-instruct")
                .build();
    }
}
