
package com.interviewAi.Backend.controler;

import com.interviewAi.Backend.service.EmailVerificationService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class EmailVerificationController {

    private final EmailVerificationService verificationService;

    public EmailVerificationController(
            EmailVerificationService verificationService) {
        this.verificationService = verificationService;
    }

    // Email link se token verify hoga
    @GetMapping(
            value = "/verify-email",
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> verifyEmail(
            @RequestParam String token) {

        verificationService.verifyEmail(token);

        return ResponseEntity.ok(
                "Email verified successfully. You can now log in to InterViewAI.");
    }
}

