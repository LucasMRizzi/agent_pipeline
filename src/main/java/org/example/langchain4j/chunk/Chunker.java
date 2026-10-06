package org.example.langchain4j.chunk;


import java.util.List;

public interface Chunker {

    List<Chunk> split(String text);

}
