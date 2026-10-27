
package com.interviewAi.Backend.service;

import com.interviewAi.Backend.entity.EmailVerificationToken;
import com.interviewAi.Backend.entity.User;
import com.interviewAi.Backend.repository.EmailVerificationTokenRepository;
import com.interviewAi.Backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class EmailVerificationService {

    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final ResendEmailService resendEmailService;
    private final String frontendUrl;

    public EmailVerificationService(
            UserRepository userRepository,
            EmailVerificationTokenRepository tokenRepository,
            ResendEmailService resendEmailService,
            @Value("${app.frontend-url}") String frontendUrl) {

        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.resendEmailService = resendEmailService;
        this.frontendUrl = frontendUrl;
    }

    @Transactional
    public void createAndSendToken(User user) {

        // User ke purane verification tokens remove karo
        tokenRepository.deleteByUserId(user.getId());

        //  Secure random token generate karo
        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);

        String token = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        EmailVerificationToken verificationToken =
                new EmailVerificationToken();

        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiresAt(
                LocalDateTime.now().plusHours(24));

        tokenRepository.save(verificationToken);

        // Link ko frontend verification page par bhejo
        String verificationLink = frontendUrl
                + "/verify-email?token=" + token;

        resendEmailService.sendVerificationEmail(
                user.getEmail(), verificationLink);
    }

    @Transactional
    public void verifyEmail(String token) {

        // Token database mein find karo
        EmailVerificationToken verificationToken =
                tokenRepository.findByToken(token)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Invalid or already used verification link"));

        // Expired link reject karo
        if (verificationToken.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            tokenRepository.delete(verificationToken);

            throw new IllegalArgumentException(
                    "Verification link has expired. Please register again or request a new link.");
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);

        userRepository.save(user);

        //  One-time token ko use ke baad remove karo
        tokenRepository.delete(verificationToken);
    }
}

