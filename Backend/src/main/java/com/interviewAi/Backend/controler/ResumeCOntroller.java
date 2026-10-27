package com.interviewAi.Backend.controler;

import com.interviewAi.Backend.dto.ResumeCheckRequest;
import com.interviewAi.Backend.dto.ResumeCheckResponse;
import com.interviewAi.Backend.service.ResumeService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/resume")
public class ResumeCOntroller {

    private final ResumeService resumeService;

    public ResumeCOntroller(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    // 1. Text analysis endpoint
    @PostMapping("/analyze")
    public ResponseEntity<ResumeCheckResponse> analyzeResume(
            @Valid @RequestBody ResumeCheckRequest request) {

        return ResponseEntity.ok(resumeService.analyzeResume(request));
    }

    // 2. File upload analysis endpoint (Multipart)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResumeCheckResponse> uploadResumeFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "jobDescription", required = false) String jobDescription) {

        try {
            ResumeCheckResponse response = resumeService.analyzeResumeFile(file, jobDescription);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            // Crash na ho, fallback report return kare
            return ResponseEntity.ok(resumeService.analyzeResume(new ResumeCheckRequest()));
        }
    }
}