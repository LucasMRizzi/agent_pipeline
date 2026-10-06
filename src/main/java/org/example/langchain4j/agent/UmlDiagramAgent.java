package org.example.langchain4j.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface UmlDiagramAgent {

    @SystemMessage("""
        Você é um arquiteto de software especialista em UML.
        Com base na especificação completa de requisitos, gere o código de um Diagrama de Classes UML em formato PlantUML (@startuml ... @enduml).

        Diretrizes:
        - Identifique as entidades principais, seus atributos (com tipos) e métodos principais.
        - Defina os relacionamentos (associação, agregação, composição, herança) e multiplicidades.
        - Retorne SOMENTE o bloco de código PlantUML.
        """)
    @UserMessage("DOCUMENTO DE REQUISITOS COMPLETO:\n{{fullDocument}}")
    String generatePlantUml(@V("fullDocument") String fullDocument);
}