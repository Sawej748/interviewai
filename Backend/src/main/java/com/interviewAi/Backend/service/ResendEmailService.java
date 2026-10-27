
package com.interviewAi.Backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class ResendEmailService {

    private final RestClient restClient;
    private final String apiKey;
    private final String fromEmail;

    public ResendEmailService(
            @Value("${resend.api.key}") String apiKey,
            @Value("${resend.from-email}") String fromEmail) {

        this.apiKey = apiKey;
        this.fromEmail = fromEmail;

        //  Resend email API client
        this.restClient = RestClient.builder()
                .baseUrl("https://api.resend.com")
                .build();
    }

    public void sendVerificationEmail(String recipient, String link) {

        String html = """
                <h2>Verify your InterViewAI account</h2>
                <p>Click the link below to verify your email address.</p>
                <p><a href="%s">Verify my email</a></p>
                <p>This link expires in 24 hours.</p>
                """.formatted(link);

        //  Resend ko email send request
        restClient.post()
                .uri("/emails")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + apiKey)
                .body(Map.of(
                        "from", fromEmail,
                        "to", new String[]{recipient},
                        "subject", "Verify your InterViewAI email",
                        "html", html
                ))
                .retrieve()
                .toBodilessEntity();
    }
}

