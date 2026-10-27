package org.example.langchain4j.agent;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface UmlDiagramAgent {

    @SystemMessage("""
        You generate one complete and coherent PlantUML class diagram.

        The requirements are authoritative.
        Retrieved patterns are examples only. Do not copy domain entities
        that are not supported by the requirements.

        Reuse class names consistently across the complete diagram.
        Include attributes, methods, relationships and multiplicities only
        when justified by the requirements.

        Return only valid PlantUML enclosed by @startuml and @enduml.
        """)
    @UserMessage("""
        UPDATED REQUIREMENTS:
        {{requirements}}

        REFERENCE UML PATTERNS:
        {{patterns}}
        """)
    String generatePlantUml(
            @V("requirements") String requirements,
            @V("patterns") String patterns
    );
}