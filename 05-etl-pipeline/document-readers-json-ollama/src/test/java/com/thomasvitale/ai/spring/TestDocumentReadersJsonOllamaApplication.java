package com.thomasvitale.ai.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.ollama.OllamaContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestDocumentReadersJsonOllamaApplication {

    @Bean
    @RestartScope
    @ServiceConnection
    OllamaContainer ollama() {
        return new OllamaContainer(DockerImageName.parse("ghcr.io/thomasvitale/ollama-mistral")
                .asCompatibleSubstituteFor("ollama/ollama"));
    }

    public static void main(String[] args) {
        SpringApplication.from(DocumentReadersJsonOllamaApplication::main).with(TestDocumentReadersJsonOllamaApplication.class).run(args);
    }

}
