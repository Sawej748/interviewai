
package com.interviewAi.Backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewAi.Backend.ai.GeminiAIService;
import com.interviewAi.Backend.dto.AnswerEvaluationResponse;
import com.interviewAi.Backend.dto.InterviewReportResponse;
import com.interviewAi.Backend.entity.Interview;
import com.interviewAi.Backend.entity.InterviewAnswer;
import com.interviewAi.Backend.entity.InterviewQuestion;
import com.interviewAi.Backend.entity.InterviewReport;
import com.interviewAi.Backend.repository.InterviewAnswerRepository;
import com.interviewAi.Backend.repository.InterviewQuestionRepository;
import com.interviewAi.Backend.repository.InterviewReportRepository;
import com.interviewAi.Backend.repository.InterviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AiService {

    private final GeminiAIService geminiAIService;
    private final InterviewRepository interviewRepository;
    private final InterviewQuestionRepository questionRepository;
    private final InterviewAnswerRepository answerRepository;
    private final InterviewReportRepository reportRepository;
    private final ObjectMapper objectMapper;

    public AiService(
            GeminiAIService geminiAIService,
            InterviewRepository interviewRepository,
            InterviewQuestionRepository questionRepository,
            InterviewAnswerRepository answerRepository,
            InterviewReportRepository reportRepository,
            ObjectMapper objectMapper) {

        this.geminiAIService = geminiAIService;
        this.interviewRepository = interviewRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.reportRepository = reportRepository;
        this.objectMapper = objectMapper;
    }

    // Answer evaluate karke database mein save karna
    @Transactional
    public AnswerEvaluationResponse submitAnswer(
            Long interviewId,
            Long questionId,
            String answerText,
            String email) {

        // Interview fetch karte hain
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Interview not found"));

        // Logged-in user interview ka owner hona chahiye
        if (interview.getUser() == null
                || !interview.getUser().getEmail().equals(email)) {
            throw new IllegalArgumentException(
                    "You cannot access this interview");
        }

        // Question fetch karte hain
        InterviewQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Question not found"));

        // Verify question isi interview ka hai
        if (question.getInterview() == null
                || !question.getInterview().getId().equals(interviewId)) {
            throw new IllegalArgumentException(
                    "Question does not belong to this interview");
        }


        // Gemini ke liye evaluation prompt
        String prompt = """
                Evaluate the student's answer to this technical interview question.

                Question: %s
                Student Answer: %s

                Evaluate technical correctness, relevance, clarity,
                and important missing concepts.

                Return ONLY a valid JSON object with these fields:
                {
                  "score": 0,
                  "feedback": "Explanation of the score",
                  "strengths": "What the student did well",
                  "improvements": "What should be improved"
                }

                Rules:
                - Score must be an integer from 0 to 100.
                - Empty or irrelevant answers should receive a low score.
                - Do not give full marks to incomplete answers.
                - Do not include Markdown fences or extra text.
                """.formatted(question.getQuestionText(), answerText);

        // Existing Gemini service ko reuse karte hain
        String aiResponse = geminiAIService.generateQuestions(prompt);

        JsonNode evaluation = parseEvaluation(aiResponse);

        int score = evaluation.path("score").asInt(-1);

        if (score < 0 || score > 100) {
            throw new IllegalStateException(
                    "Gemini returned an invalid score");
        }

        String feedback = requiredText(evaluation, "feedback");
        String strengths = requiredText(evaluation, "strengths");
        String improvements = requiredText(evaluation, "improvements");

        // Answer aur AI evaluation save karte hain
        InterviewAnswer answer = answerRepository
                .findByQuestionId(questionId)
                .orElseGet(InterviewAnswer::new);

        answer.setQuestion(question);
        answer.setAnswerText(answerText.trim());
        answer.setScore(score);
        answer.setFeedback(feedback);
        answer.setStrengths(strengths);
        answer.setImprovements(improvements);

        InterviewAnswer savedAnswer = answerRepository.save(answer);

        // Overall interview report update karte hain
        updateReport(interview);

        return new AnswerEvaluationResponse(
                savedAnswer.getId(),
                question.getId(),
                question.getQuestionText(),
                savedAnswer.getAnswerText(),
                savedAnswer.getScore(),
                savedAnswer.getFeedback(),
                savedAnswer.getStrengths(),
                savedAnswer.getImprovements()
        );
    }

    // Interview ki saved report return karna
    @Transactional(readOnly = true)
    public InterviewReportResponse getReport(
            Long interviewId, String email) {

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Interview not found"));

        if (interview.getUser() == null
                || !interview.getUser().getEmail().equals(email)) {
            throw new IllegalArgumentException(
                    "You cannot access this interview");
        }

        InterviewReport report = reportRepository
                .findByInterviewId(interviewId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Report not available. Submit an answer first."));

        return toReportResponse(report);
    }

    // Submitted answers ke basis par summary recalculate karna
    private void updateReport(Interview interview) {

        Long interviewId = interview.getId();

        int totalQuestions = questionRepository
                .findByInterviewIdOrderByQuestionOrderAsc(interviewId)
                .size();

        List<InterviewAnswer> answers =
                answerRepository.findByQuestionInterviewId(interviewId);

        double averageScore = answers.stream()
                .mapToInt(InterviewAnswer::getScore)
                .average()
                .orElse(0.0);

        String strengths = answers.stream()
                .map(InterviewAnswer::getStrengths)
                .filter(value -> value != null && !value.isBlank())
                .distinct()
                .reduce((a, b) -> a + "; " + b)
                .orElse("No strengths available yet");

        String improvements = answers.stream()
                .map(InterviewAnswer::getImprovements)
                .filter(value -> value != null && !value.isBlank())
                .distinct()
                .reduce((a, b) -> a + "; " + b)
                .orElse("No improvements available yet");

        InterviewReport report = reportRepository
                .findByInterviewId(interviewId)
                .orElseGet(InterviewReport::new);

        report.setInterview(interview);
        report.setTotalQuestions(totalQuestions);
        report.setAnsweredQuestions(answers.size());
        report.setAverageScore(
                Math.round(averageScore * 100.0) / 100.0);
        report.setStrengths(strengths);
        report.setImprovements(improvements);

        reportRepository.save(report);
    }

    private InterviewReportResponse toReportResponse(
            InterviewReport report) {

        return new InterviewReportResponse(
                report.getInterview().getId(),
                report.getTotalQuestions(),
                report.getAnsweredQuestions(),
                report.getAverageScore(),
                report.getStrengths(),
                report.getImprovements()
        );
    }

    // Gemini ke JSON response ko parse karna
    private JsonNode parseEvaluation(String response) {
        try {
            int start = response.indexOf('{');
            int end = response.lastIndexOf('}');

            if (start < 0 || end <= start) {
                throw new IllegalStateException(
                        "Gemini did not return valid JSON");
            }

            return objectMapper.readTree(
                    response.substring(start, end + 1));

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Could not parse Gemini evaluation response",
                    exception);
        }
    }

    private String requiredText(JsonNode json, String field) {
        String value = json.path(field).asText("").trim();

        if (value.isBlank()) {
            throw new IllegalStateException(
                    "Gemini response is missing field: " + field);
        }

        return value;
    }
}
