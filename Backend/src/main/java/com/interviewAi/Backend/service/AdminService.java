package com.interviewAi.Backend.service;

import com.interviewAi.Backend.dto.AdminStatsResponse;
import com.interviewAi.Backend.entity.Interview;
import com.interviewAi.Backend.entity.InterviewReport;
import com.interviewAi.Backend.entity.User;
import com.interviewAi.Backend.repository.InterviewQuestionRepository;
import com.interviewAi.Backend.repository.InterviewReportRepository;
import com.interviewAi.Backend.repository.InterviewRepository;
import com.interviewAi.Backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final InterviewRepository interviewRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;
    private final InterviewReportRepository interviewReportRepository;

    public AdminService(
            UserRepository userRepository,
            InterviewRepository interviewRepository,
            InterviewQuestionRepository interviewQuestionRepository,
            InterviewReportRepository interviewReportRepository) {
        this.userRepository = userRepository;
        this.interviewRepository = interviewRepository;
        this.interviewQuestionRepository = interviewQuestionRepository;
        this.interviewReportRepository = interviewReportRepository;
    }

    // 1. Database se LIVE Real Stats nikalna
    public AdminStatsResponse getPlatformStats() {
        long totalUsers = userRepository.count();
        long totalInterviews = interviewRepository.count();
        long totalQuestions = interviewQuestionRepository.count();

        // Database me bane hue actual reports se average calculate karna (Double handling fix)
        List<InterviewReport> reports = interviewReportRepository.findAll();
        double avgScore = reports.stream()
                .filter(r -> r.getAverageScore() != null)
                .mapToDouble(InterviewReport::getAverageScore)
                .average()
                .orElse(0.0);

        return new AdminStatsResponse(
                totalUsers,
                totalInterviews,
                totalQuestions,
                Math.round(avgScore * 10.0) / 10.0
        );
    }

    // 2. Database ke saare real users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 3. User Role toggle
    @Transactional
    public User updateUserRole(Long userId, String newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        user.setRole(newRole.toUpperCase());
        return userRepository.save(user);
    }

    // 4. User Delete
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
        userRepository.deleteById(userId);
    }

    // 5. Database ke saare real interviews
    public List<Interview> getAllInterviews() {
        return interviewRepository.findAll();
    }
}