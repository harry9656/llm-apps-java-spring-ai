package com.thomasvitale.ai.spring;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
class ChatService {

    private final ChatClient chatClient;

    private final Resource systemMessageResource;

    ChatService(ChatClient chatClient, @Value("classpath:/prompts/system-message.st") Resource systemMessageResource) {
        this.chatClient = chatClient;
        this.systemMessageResource = systemMessageResource;
    }

    String chatWithUserMessageTemplate(MusicQuestion question) {
        var userPromptTemplate = new PromptTemplate("""
                Tell me name and band of three musicians famous for playing in a {genre} band.
                Consider only the musicians that play the {instrument} in that band.
                """);
        Map<String,Object> model = Map.of("instrument", question.instrument(), "genre", question.genre());
        var userMessage = userPromptTemplate.createMessage(model);

        return chatClient.call(userMessage);
    }

    String chatWithSystemMessageTemplate(String message) {
        var systemPromptTemplate = new SystemPromptTemplate("""
                You are a helpful assistant that always replies starting with {greeting}.
                """);
        Map<String,Object> model = Map.of("greeting", randomGreeting());
        var systemMessage = systemPromptTemplate.createMessage(model);

        var userMessage = new UserMessage(message);

        return chatClient.call(systemMessage, userMessage);
    }

    String chatWithSystemMessageTemplateExternal(String message) {
        var systemPromptTemplate = new SystemPromptTemplate(systemMessageResource);
        Map<String,Object> model = Map.of("greeting", randomGreeting());
        var systemMessage = systemPromptTemplate.createMessage(model);

        var userMessage = new UserMessage(message);

        return chatClient.call(systemMessage, userMessage);
    }

    private String randomGreeting() {
        var names = List.of("Howdy", "Ahoy", "Well, well, well");
        return names.get(new Random().nextInt(names.size()));
    }

}
