
package com.interviewAi.Backend.repository;

import com.interviewAi.Backend.entity.Interview;
import com.interviewAi.Backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewRepository
        extends JpaRepository<Interview, Long> {

    // Logged-in user ke interviews fetch karna
    List<Interview> findByUserOrderByCreatedAtDesc(User user);
}

