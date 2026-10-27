
package com.interviewAi.Backend.service;

import com.interviewAi.Backend.dto.CreateQuestionRequest;
import com.interviewAi.Backend.dto.InterviewQuestionResponse;
import com.interviewAi.Backend.entity.Interview;
import com.interviewAi.Backend.entity.InterviewQuestion;
import com.interviewAi.Backend.repository.InterviewQuestionRepository;
import com.interviewAi.Backend.repository.InterviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InterviewQuestionService {

    private final InterviewQuestionRepository questionRepository;
    private final InterviewRepository interviewRepository;

    //  Required dependencies
    public InterviewQuestionService(
            InterviewQuestionRepository questionRepository,
            InterviewRepository interviewRepository) {
        this.questionRepository = questionRepository;
        this.interviewRepository = interviewRepository;
    }

    //  Question save karna
    @Transactional
    public InterviewQuestionResponse addQuestion(
            Long interviewId,
            CreateQuestionRequest request,
            String email) {

        Interview interview = getOwnedInterview(interviewId, email);

        InterviewQuestion question = new InterviewQuestion();
        question.setQuestionText(request.getQuestionText().trim());
        question.setQuestionOrder(request.getQuestionOrder());
        question.setInterview(interview);

        InterviewQuestion saved = questionRepository.save(question);

        return toResponse(saved);
    }

    //  Questions ki list fetch karna
    @Transactional(readOnly = true)
    public List<InterviewQuestionResponse> getQuestions(
            Long interviewId, String email) {

        getOwnedInterview(interviewId, email);

        return questionRepository
                .findByInterviewIdOrderByQuestionOrderAsc(interviewId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    //  Interview ownership check
    private Interview getOwnedInterview(Long interviewId, String email) {

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Interview not found"));

        if (interview.getUser() == null
                || !interview.getUser().getEmail().equals(email)) {
            throw new IllegalArgumentException(
                    "You cannot access this interview");
        }

        return interview;
    }

    // Response DTO banana
    private InterviewQuestionResponse toResponse(
            InterviewQuestion question) {

        return new InterviewQuestionResponse(
                question.getId(),
                question.getInterview().getId(),
                question.getQuestionText(),
                question.getQuestionOrder()
        );
    }
}

