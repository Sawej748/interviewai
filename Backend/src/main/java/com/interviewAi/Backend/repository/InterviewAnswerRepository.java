
package com.interviewAi.Backend.repository;

import com.interviewAi.Backend.entity.InterviewAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InterviewAnswerRepository
        extends JpaRepository<InterviewAnswer, Long> {

    // Interview ke saare submitted answers fetch karna
    List<InterviewAnswer> findByQuestionInterviewId(Long interviewId);

    //  Kisi specific question ka saved answer fetch karna
    Optional<InterviewAnswer> findByQuestionId(Long questionId);

    // Check karna ki question ka answer pehle submit hua hai ya nahi
    boolean existsByQuestionId(Long questionId);
}
