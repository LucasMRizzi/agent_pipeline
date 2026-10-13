package org.example.langchain4j.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface RequirementTranslatorAgent {
    
    @SystemMessage("pt-br-en")
    @UserMessage("{{textoEmPortugues}}")
    String rewrite(@V("textoEmPortugues") String textoEmPortugues);
}
