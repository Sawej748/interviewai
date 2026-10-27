
package com.interviewAi.Backend.controler;

import com.interviewAi.Backend.dto.InterviewRequest;
import com.interviewAi.Backend.dto.InterviewResponse;
import com.interviewAi.Backend.service.InterviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
public class InterviewController {

    private final InterviewService interviewService;

    //  Service inject karna
    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    //  Interview create API
    @PostMapping
    public ResponseEntity<InterviewResponse> createInterview(
            @Valid @RequestBody InterviewRequest request,
            Authentication authentication) {

        InterviewResponse response = interviewService.createInterview(
                request, authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Current user's interview history
    @GetMapping("/my")
    public ResponseEntity<List<InterviewResponse>> getMyInterviews(
            Authentication authentication) {

        return ResponseEntity.ok(
                interviewService.getMyInterviews(authentication.getName()));
    }
}
