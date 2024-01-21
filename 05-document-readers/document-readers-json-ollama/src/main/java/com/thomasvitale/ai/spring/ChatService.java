package com.thomasvitale.ai.spring;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.prompt.Prompt;
import org.springframework.ai.prompt.SystemPromptTemplate;
import org.springframework.ai.prompt.messages.AssistantMessage;
import org.springframework.ai.prompt.messages.UserMessage;
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
        var systemPromptTemplate = new SystemPromptTemplate("""
                You're assisting with questions about products in a bicycle catalog.
                Use the information from the DOCUMENTS section to provide accurate answers.
                If the answer involves referring to the price or the dimension of the bicycle,
                include the bicycle name in the response.
                If unsure, simply state that you don't know.
                                
                DOCUMENTS:
                {documents}
                """);

        List<Document> similarDocuments = vectorStore.similaritySearch(SearchRequest.query(message).withTopK(2));
        String documents = similarDocuments.stream().map(Document::getContent).collect(Collectors.joining("\n"));

        Map<String,Object> model = Map.of("documents", documents);
        var systemMessage = systemPromptTemplate.createMessage(model);

        var userMessage = new UserMessage(message);

        var prompt = new Prompt(List.of(systemMessage, userMessage));

        var chatResponse = chatClient.generate(prompt);
        return new AssistantMessage(chatResponse.getGeneration().getContent(), chatResponse.getGeneration().getProperties());
    }

}
