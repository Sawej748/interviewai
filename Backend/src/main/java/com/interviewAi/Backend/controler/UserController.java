package com.interviewAi.Backend.controler;

import com.interviewAi.Backend.dto.RegisterRequest;
import com.interviewAi.Backend.dto.UserResponse;
import com.interviewAi.Backend.entity.User;
import com.interviewAi.Backend.repository.UserRepository;
import com.interviewAi.Backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;


    // Spring automatically UserService inject karega
    public UserController(UserService userService
                      ,UserRepository userRepository)
    {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(
            @Valid @RequestBody RegisterRequest request) {

        // @RequestBody JSON ko RegisterRequest mein convert karta hai
        // @Valid DTO ki validation rules check karta hai
        User savedUser = userService.registerUser(request);

        // Password exclude karke safe response banata hai
        UserResponse response = new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail()
        );

        // Successful creation ke liye HTTP 201 return karta hai
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

   // Logged-in user ki profile return karta hai
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(
            Authentication authentication) {

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .map(user -> ResponseEntity.ok(
                        new UserResponse(
                                user.getId(),
                                user.getName(),
                                user.getEmail()
                        )))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}