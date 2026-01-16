package com.texttolearn.backend.config;

import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.nio.charset.StandardCharsets;

@Configuration
public class AiConfig {

    @Bean
    public RestClientCustomizer restClientCustomizer() {
        return restClientBuilder -> restClientBuilder.requestInterceptor((request, body, execution) -> {
            String originalBody = new String(body, StandardCharsets.UTF_8);
            
            // This logic rips out the 'extra_body' field that Cerebras hates
            String cleanedBody = originalBody
                .replace(",\"extra_body\":{}", "")
                .replace("\"extra_body\":{},", "")
                .replace("\"extra_body\":{}", "");

            return execution.execute(request, cleanedBody.getBytes(StandardCharsets.UTF_8));
        });
    }
}
