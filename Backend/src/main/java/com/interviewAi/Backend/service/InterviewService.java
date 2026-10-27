
package com.interviewAi.Backend.service;

import com.interviewAi.Backend.dto.InterviewRequest;
import com.interviewAi.Backend.dto.InterviewResponse;
import com.interviewAi.Backend.entity.Interview;
import com.interviewAi.Backend.entity.User;
import com.interviewAi.Backend.repository.InterviewRepository;
import com.interviewAi.Backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final UserRepository userRepository;

    //  Required dependencies
    public InterviewService(
            InterviewRepository interviewRepository,
            UserRepository userRepository) {
        this.interviewRepository = interviewRepository;
        this.userRepository = userRepository;
    }


    //  Naya interview create karna
    @Transactional
    public InterviewResponse createInterview(
            InterviewRequest request, String email) {

        // Token se mile email ke through logged-in user find karo
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        Interview interview = new Interview();
        interview.setTitle(request.getTitle().trim());
        interview.setTopic(request.getTopic());
        interview.setJobDescription(request.getJobDescription());
        interview.setUser(user);

        Interview saved = interviewRepository.save(interview);

        return toResponse(saved);
    }

    //  Current user ki interview history
    @Transactional(readOnly = true)
    public List<InterviewResponse> getMyInterviews(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        return interviewRepository
                .findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    //  Entity ko safe response mein convert karna
    private InterviewResponse toResponse(Interview interview) {
        return new InterviewResponse(
                interview.getId(),
                interview.getTitle(),
                interview.getTopic(),
                interview.getStatus(),
                interview.getCreatedAt()
        );
    }
}
