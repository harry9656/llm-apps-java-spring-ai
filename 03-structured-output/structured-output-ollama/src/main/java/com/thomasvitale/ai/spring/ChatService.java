package com.thomasvitale.ai.spring;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Chat examples using the high-level ChatClient API.
 */
@Service
class ChatService {

    private final ChatClient chatClient;

    ChatService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    ArtistInfo chatWithBeanOutput(MusicQuestion question) {
        var userPromptTemplate = """
                Tell me name and band of one musician famous for playing in a {genre} band.
                Consider only the musicians that play the {instrument} in that band.
                """;

        return chatClient.prompt()
                .user(userSpec -> userSpec
                        .text(userPromptTemplate)
                        .param("genre", question.genre())
                        .param("instrument", question.instrument())
                )
                .options(OllamaOptions.builder()
                        .withFormat("json")
                        .build())
                .call()
                .entity(ArtistInfo.class);
    }

    Map<String,Object> chatWithMapOutput(MusicQuestion question) {
        var userPromptTemplate = """
                Tell me the names of three musicians famous for playing in a {genre} band.
                Consider only the musicians that play the {instrument} in that band.
                """;

        return chatClient.prompt()
                .user(userSpec -> userSpec
                        .text(userPromptTemplate)
                        .param("genre", question.genre())
                        .param("instrument", question.instrument())
                )
                .call()
                .entity(new ParameterizedTypeReference<>() {});
    }

    List<String> chatWithListOutput(MusicQuestion question) {
        var userPromptTemplate = """
                Tell me the names of three musicians famous for playing in a {genre} band.
                Consider only the musicians that play the {instrument} in that band.
                """;

        return chatClient.prompt()
                .user(userSpec -> userSpec
                        .text(userPromptTemplate)
                        .param("genre", question.genre())
                        .param("instrument", question.instrument())
                )
                .call()
                .entity(new ListOutputConverter(new DefaultConversionService()));
    }

}
