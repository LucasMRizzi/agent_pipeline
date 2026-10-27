package org.example.langchain4j.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface RequirementsRewriterAgent {

    @SystemMessage("""
        Você é um especialista em documentação de software.
        Sua tarefa é reescrever o TRECHO ORIGINAL de requisitos aplicando as correções sugeridas na ANÁLISE.

        Regras:
        - Mantenha um tom técnico, formal e preciso (padrão IEEE/Engenharia de Requisitos).
        - Retorne APENAS o texto do requisito corrigido, sem explicações ou introduções.
        """)
    @UserMessage("""
        TRECHO ORIGINAL:
        {{originalChunk}}

        ANÁLISE E CORREÇÕES SUGERIDAS:
        {{analysis}}
        """)
    String rewrite(@V("originalChunk") String originalChunk, @V("analysis") String analysis);
}