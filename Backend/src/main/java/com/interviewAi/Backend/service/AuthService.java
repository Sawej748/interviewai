package com.interviewAi.Backend.service;

import com.interviewAi.Backend.dto.AuthResponse;
import com.interviewAi.Backend.dto.GoogleAuthRequest;
import com.interviewAi.Backend.dto.LoginRequest;
import com.interviewAi.Backend.entity.User;
import com.interviewAi.Backend.repository.UserRepository;
import com.interviewAi.Backend.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            JwtService jwtService,
            PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    // 1. Regular Email/Password Login
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole());

        return new AuthResponse(token, user.getEmail(), user.getRole());
    }

    // 2. Continue with Google Authentication (Real-World OAuth flow)
    @Transactional
    public AuthResponse loginWithGoogle(GoogleAuthRequest request) {
        // Check karo user pehle se database me hai ya nahi
        User user = userRepository.findByEmail(request.getEmail())
                .orElseGet(() -> {
                    // Agar naya user hai to direct account create karo
                    User newUser = new User();
                    newUser.setName(request.getName());
                    newUser.setEmail(request.getEmail());
                    // Google user ke liye random secure encrypted password set karte hain
                    newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    newUser.setRole("USER");
                    return userRepository.save(newUser);
                });

        // App ka JWT token generate karke return karo
        String token = jwtService.generateToken(user.getEmail(), user.getRole());
        return new AuthResponse(token, user.getEmail(), user.getRole());
    }
}