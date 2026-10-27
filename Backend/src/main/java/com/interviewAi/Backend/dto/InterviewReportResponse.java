
package com.interviewAi.Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InterviewReportResponse {

    private Long interviewId;
    private Integer totalQuestions;
    private Integer answeredQuestions;
    private Double averageScore;
    private String strengths;
    private String improvements;
}

