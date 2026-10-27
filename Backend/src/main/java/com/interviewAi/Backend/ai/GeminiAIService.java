
package com.interviewAi.Backend.ai;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GeminiAIService {

    private final Client geminiClient;

    // API key configuration se lekar Gemini client banate hain
    public GeminiAIService(
            @Value("${gemini.api.key}") String apiKey) {

        this.geminiClient = Client.builder()
                .apiKey(apiKey)
                .build();
    }

    // Prompt bhejkar Gemini ka text response return karta hai
    public String generateQuestions(String prompt) {

        GenerateContentResponse response =
                geminiClient.models.generateContent(
                        "gemini-3.8-flash",
                        prompt,
                        null
                );

        String result = response.text();

        if (result == null || result.isBlank()) {
            throw new IllegalStateException(
                    "Gemini returned an empty response."
            );
        }

        return result;
    }
}

