package com.thomasvitale.ai.spring;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.prompt.Prompt;
import org.springframework.ai.prompt.PromptTemplate;
import org.springframework.ai.prompt.messages.AssistantMessage;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
class ChatService {

    private final ChatClient chatClient;
    private final SimpleVectorStore vectorStore;

    ChatService(ChatClient chatClient, SimpleVectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }

    AssistantMessage chatWithDocument(String message) {
        var promptTemplate = new PromptTemplate("""
                Answer questions given the information below (DOCUMENTS section) and no prior knowledge.
                In the answer, include the source file name from which the context information is extracted from.
                                
                DOCUMENTS:
                {documents}
                
                Given the context information and no prior knowledge, answer the question (QUESTION section).
                
                QUESTION:
                {question}
                """);

        List<Document> similarDocuments = vectorStore.similaritySearch(SearchRequest.query(message).withTopK(2));
        String documents = similarDocuments.stream().map(Document::getContent).collect(Collectors.joining("\n"));

        Map<String,Object> model = Map.of("documents", documents, "question", message);
        var userMessage = promptTemplate.createMessage(model);

        var prompt = new Prompt(userMessage);

        var chatResponse = chatClient.generate(prompt);
        return new AssistantMessage(chatResponse.getGeneration().getContent(), chatResponse.getGeneration().getProperties());
    }

}
