package org.example.langchain4j.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.IngestionResult;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.example.langchain4j.chunk.Chunk;
import org.example.langchain4j.chunk.RequirementChunker;

import java.util.ArrayList;
import java.util.List;

public class RAGFactoryRequirements implements RAGFactory {

    private final RequirementChunker chunker;


    public RAGFactoryRequirements(int maxCharacters) {
        this.chunker = new RequirementChunker(maxCharacters);
    }

    @Override
    public RAG create(EmbeddingModel embeddingModel){

        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        EmbeddingStoreIngestor ingestor =
                new EmbeddingStoreIngestor(
                        null,
                        null,
                        null,
                        embeddingModel,
                        embeddingStore
                ) {

                    @Override
                    public IngestionResult ingest(
                            Document document
                    ) {

                        List<Chunk> chunks = chunker.split(document.text());

                        List<Document> chunkDocuments = new ArrayList<>();

                        for (Chunk chunk : chunks) {
                            chunkDocuments.add(Document.from(chunk.segment().text()));
                        }

                        return super.ingest(chunkDocuments);
                    }

                    @Override
                    public IngestionResult ingest(
                            List<Document> documents
                    ) {

                        List<Document> chunkDocuments = new ArrayList<>();

                        for (Document document : documents) {

                            List<Chunk> chunks = chunker.split(document.text());

                            for (Chunk chunk : chunks) {
                                chunkDocuments.add(Document.from(chunk.segment().text()));
                            }
                        }

                        return super.ingest(chunkDocuments);
                    }
                };

        EmbeddingStoreContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
                        .embeddingStore(embeddingStore)
                        .embeddingModel(embeddingModel)
                        .maxResults(3)
                        .build();

        return new RAG(
                embeddingStore,
                ingestor,
                retriever
        );
    }
}
