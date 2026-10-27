
package com.interviewAi.Backend.controler;

import com.interviewAi.Backend.dto.CreateQuestionRequest;
import com.interviewAi.Backend.dto.InterviewQuestionResponse;
import com.interviewAi.Backend.service.InterviewQuestionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interviews/{interviewId}/questions")
public class InterviewQuestionController {

    private final InterviewQuestionService questionService;

    //  Service inject karna
    public InterviewQuestionController(
            InterviewQuestionService questionService) {
        this.questionService = questionService;
    }

    // Question create API
    @PostMapping
    public ResponseEntity<InterviewQuestionResponse> addQuestion(
            @PathVariable Long interviewId,
            @Valid @RequestBody CreateQuestionRequest request,
            Authentication authentication) {

        InterviewQuestionResponse response = questionService.addQuestion(
                interviewId, request, authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Interview questions fetch API
    @GetMapping
    public ResponseEntity<List<InterviewQuestionResponse>> getQuestions(
            @PathVariable Long interviewId,
            Authentication authentication) {

        return ResponseEntity.ok(
                questionService.getQuestions(
                        interviewId, authentication.getName()));
    }
}

