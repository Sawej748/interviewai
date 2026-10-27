
package com.interviewAi.Backend.controler;

import com.interviewAi.Backend.dto.AnswerEvaluationResponse;
import com.interviewAi.Backend.dto.InterviewReportResponse;
import com.interviewAi.Backend.dto.SubmitAnswerRequest;
import com.interviewAi.Backend.service.AiService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interviews")
public class AiAnswerController {

    private final AiService aiService;

    // Evaluation service inject karte hain
    public AiAnswerController(AiService aiService) {
        this.aiService = aiService;
    }

    // Student ka answer submit aur evaluate karna
    @PostMapping("/{interviewId}/questions/{questionId}/answer")
    @ResponseStatus(HttpStatus.CREATED)
    public AnswerEvaluationResponse submitAnswer(
            @PathVariable Long interviewId,
            @PathVariable Long questionId,
            @Valid @RequestBody SubmitAnswerRequest request,
            Authentication authentication) {

        return aiService.submitAnswer(
                interviewId,
                questionId,
                request.getAnswerText(),
                authentication.getName()
        );
    }

    // Interview ki evaluation report fetch karna
    @GetMapping("/{interviewId}/report")
    public InterviewReportResponse getReport(
            @PathVariable Long interviewId,
            Authentication authentication) {

        return aiService.getReport(
                interviewId,
                authentication.getName()
        );
    }
}

