package org.example.langchain4j.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface RequirementsAnalyzerAgent {

    @SystemMessage("""
        Você é um engenheiro de requisitos sênior.
        Sua função é analisar o trecho do documento recebido e identificar:
        - ambiguidades;
        - incompletudes;
        - inconsistências;
        - problemas de verificabilidade.

        Para cada problema, proponha uma correção clara e direta.
        Retorne somente o resultado da análise.
        """)
    @UserMessage("TRECHO:\n{{chunkText}}")
    String analyze(@V("chunkText") String chunkText);
}