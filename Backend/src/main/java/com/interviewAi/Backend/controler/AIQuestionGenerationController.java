
package com.interviewAi.Backend.controler;

import com.interviewAi.Backend.dto.GenerateQuestionsRequest;
import com.interviewAi.Backend.dto.InterviewQuestionResponse;
import com.interviewAi.Backend.service.AIQuestionGenerationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
public class AIQuestionGenerationController {

    private final AIQuestionGenerationService generationService;

    // Service inject karte hain
    public AIQuestionGenerationController(
            AIQuestionGenerationService generationService) {
        this.generationService = generationService;
    }

    // Existing interview ke liye AI questions generate karta hai
    @PostMapping("/{interviewId}/questions/generate")
    @ResponseStatus(HttpStatus.CREATED)
    public List<InterviewQuestionResponse> generateQuestions(
            @PathVariable Long interviewId,
            @Valid @RequestBody GenerateQuestionsRequest request,
            Authentication authentication) {

        return generationService.generateQuestions(
                interviewId,
                request,
                authentication.getName()
        );
    }
}

