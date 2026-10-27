
package com.interviewAi.Backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    // AiService ke liye ObjectMapper bean
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}

