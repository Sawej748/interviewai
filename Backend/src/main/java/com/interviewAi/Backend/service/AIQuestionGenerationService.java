package com.interviewAi.Backend.service;

import com.interviewAi.Backend.ai.GeminiAIService;
import com.interviewAi.Backend.dto.GenerateQuestionsRequest;
import com.interviewAi.Backend.dto.InterviewQuestionResponse;
import com.interviewAi.Backend.entity.Interview;
import com.interviewAi.Backend.entity.InterviewQuestion;
import com.interviewAi.Backend.repository.InterviewQuestionRepository;
import com.interviewAi.Backend.repository.InterviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AIQuestionGenerationService {

    private final GeminiAIService geminiAIService;
    private final InterviewRepository interviewRepository;
    private final InterviewQuestionRepository questionRepository;

    public AIQuestionGenerationService(
            GeminiAIService geminiAIService,
            InterviewRepository interviewRepository,
            InterviewQuestionRepository questionRepository) {

        this.geminiAIService = geminiAIService;
        this.interviewRepository = interviewRepository;
        this.questionRepository = questionRepository;
    }

    // AI questions generate karke database mein save karna
    @Transactional
    public List<InterviewQuestionResponse> generateQuestions(
            Long interviewId,
            GenerateQuestionsRequest request,
            String email) {

        // User ka interview fetch aur ownership verify karte hain
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Interview not found"));

        if (interview.getUser() == null
                || !interview.getUser().getEmail().equals(email)) {
            throw new IllegalArgumentException(
                    "You cannot access this interview");
        }

        // Interview details se Gemini prompt banate hain
        String topic = interview.getTopic() == null || interview.getTopic().isBlank()
                ? "General technical interview"
                : interview.getTopic();

        String jobDescription = interview.getJobDescription() == null || interview.getJobDescription().isBlank()
                ? "Not provided"
                : interview.getJobDescription();

        String prompt = """
                You are an experienced technical interviewer.

                Generate exactly %d interview questions.

                Topic: %s
                Job Description: %s

                Rules:
                1. Questions must be relevant to the topic and job description.
                2. Use a mix of basic and intermediate questions.
                3. Return only questions, one per line.
                4. Number questions from 1 onwards.
                5. Do not include answers or explanations.
                6. Every question must end with a question mark.
                """.formatted(
                request.getQuestionCount(),
                topic,
                jobDescription
        );

        // Gemini se response lete hain
        String aiResponse = geminiAIService.generateQuestions(prompt);

        List<String> questions = extractQuestions(aiResponse);

        // Resilient check: Agar list khali hai tabhi error throw kare, minor count difference par crash na kare
        if (questions.isEmpty()) {
            throw new IllegalStateException(
                    "Failed to parse questions from AI response. Please try generating again.");
        }

        List<InterviewQuestionResponse> responses = new ArrayList<>();

        // Har question ko database mein save karte hain
        for (int i = 0; i < questions.size(); i++) {
            InterviewQuestion question = new InterviewQuestion();

            question.setQuestionText(questions.get(i));
            question.setQuestionOrder(i + 1);
            question.setInterview(interview);

            InterviewQuestion saved = questionRepository.save(question);

            responses.add(new InterviewQuestionResponse(
                    saved.getId(),
                    interview.getId(),
                    saved.getQuestionText(),
                    saved.getQuestionOrder()
            ));
        }

        return responses;
    }

    // AI output se numbering aur bullets hatakar clean question nikalna
    private List<String> extractQuestions(String response) {
        List<String> questions = new ArrayList<>();

        for (String line : response.split("\\R")) {
            String cleaned = line.trim();

            // Numbering (1., 2.) aur bullets (*, -) remove karna
            cleaned = cleaned.replaceFirst(
                    "^(?:[-*•]\\s*|\\d+[.)]\\s*)",
                    ""
            ).trim();

            if (!cleaned.isBlank() && cleaned.endsWith("?")) {
                questions.add(cleaned);
            }
        }

        return questions;
    }
}