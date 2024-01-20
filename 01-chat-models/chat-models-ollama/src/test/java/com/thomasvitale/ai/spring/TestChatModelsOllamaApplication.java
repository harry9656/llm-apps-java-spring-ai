package com.thomasvitale.ai.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.GenericContainer;

@TestConfiguration(proxyBeanMethods = false)
public class TestChatModelsOllamaApplication {

    @Bean
    GenericContainer<?> ollama(DynamicPropertyRegistry properties) {
        var ollama = new GenericContainer<>("ghcr.io/thomasvitale/ollama-llama2")
                .withExposedPorts(11434);
        properties.add("spring.ai.ollama.base-url",
                () -> "http://%s:%s".formatted(ollama.getHost(), ollama.getMappedPort(11434)));
        properties.add("spring.ai.ollama.chat.model", () -> "llama2");
        return ollama;
    }

    public static void main(String[] args) {
        SpringApplication.from(ChatModelsOllamaApplication::main).with(TestChatModelsOllamaApplication.class).run(args);
    }

}
