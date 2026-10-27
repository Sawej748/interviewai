

package com.interviewAi.Backend.controler;

import com.interviewAi.Backend.dto.DashboardResponse;
import com.interviewAi.Backend.service.DashboardService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    // Spring dashboard service inject karega
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // Logged-in user ka dashboard data return karta hai
    @GetMapping
    public DashboardResponse getDashboard(Authentication authentication) {

        // JWT se authenticated user ka email milta hai
        String email = authentication.getName();

        return dashboardService.getDashboard(email);
    }
}

