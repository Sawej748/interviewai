

package com.interviewAi.Backend.service;

import com.interviewAi.Backend.dto.DashboardResponse;
import com.interviewAi.Backend.entity.User;
import com.interviewAi.Backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final UserRepository userRepository;

    // Spring database repository inject karega
    public DashboardService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public DashboardResponse getDashboard(String email) {

        // Login user ko email se database mein find karte hain
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        // Frontend ke liye dashboard response banate hain
        return new DashboardResponse(
                user.getName(),
                user.getEmail(),
                "Welcome back, " + user.getName() + "!"
        );
    }
}


