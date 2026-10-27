
/* 🔴 FULL CLASS UPDATE START */
package com.interviewAi.Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnswerEvaluationResponse {

    private Long answerId;
    private Long questionId;
    private String questionText;
    private String answerText;
    private Integer score;
    private String feedback;
    private String strengths;
    private String improvements;
}

