package org.example.langchain4j.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface AnalysisConsolidatorAgent {

    @SystemMessage("""
                Você é um engenheiro de requisitos especialista
                na IEEE 29148.

                Foram realizadas análises individuais de diferentes
                partes de um documento de requisitos.

                Consolide essas análises em uma única análise.

                Regras:
                - mantenha todos os problemas relevantes;
                - remova problemas duplicados;
                - identifique problemas que aparecem entre diferentes
                  requisitos;
                - preserve a identificação dos requisitos;
                - não invente problemas que não estejam nas análises;
                - organize a resposta de forma clara.

                Análises individuais:

                %s
                """)
    @UserMessage("TRECHO:\n{{analysesText}}")
    String consolidate(@V("analysesText") String analysesText);
}
