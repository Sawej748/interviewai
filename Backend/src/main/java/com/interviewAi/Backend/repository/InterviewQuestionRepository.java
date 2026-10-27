
package com.interviewAi.Backend.repository;

import com.interviewAi.Backend.entity.InterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewQuestionRepository
        extends JpaRepository<InterviewQuestion, Long> {

    //  Interview ke questions order mein laana
    List<InterviewQuestion> findByInterviewIdOrderByQuestionOrderAsc(
            Long interviewId);
}

