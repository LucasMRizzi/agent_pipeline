package org.example.langchain4j.agent;

public interface Agent<I, O> {
    O execute(I input);
}