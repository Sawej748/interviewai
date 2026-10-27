
package com.interviewAi.Backend.repository;

import com.interviewAi.Backend.entity.InterviewReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InterviewReportRepository
        extends JpaRepository<InterviewReport, Long> {

    // Interview ki existing report find karna
    Optional<InterviewReport> findByInterviewId(Long interviewId);
}

