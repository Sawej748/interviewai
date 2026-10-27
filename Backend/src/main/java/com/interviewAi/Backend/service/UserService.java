
package com.interviewAi.Backend.service;

import com.interviewAi.Backend.dto.RegisterRequest;
import com.interviewAi.Backend.entity.User;
import com.interviewAi.Backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    //  EmailVerificationService dependency removed.
    // Demo registration ab external email service par depend nahi karega.
    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(RegisterRequest request) {

        // Email ko consistent format mein store karte hain.
        String email = request.getEmail().trim().toLowerCase();

        // Duplicate email ko reject karte hain.
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Email is already registered");
        }

        // Request se user entity banate hain.
        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(email);

        //  Password database mein hashed form mein save hoga.
        user.setPassword(
                passwordEncoder.encode(request.getPassword()));

        // Default role: normal user.
        user.setRole("USER");

        //  Demo mode mein email verification required nahi hai.
        user.setEmailVerified(false);

        // User ko database mein save karte hain.
        // Ab Resend email service call nahi hogi.
        return userRepository.save(user);
    }
}
