

package com.interviewAi.Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InterviewQuestionResponse {

    private Long id;
    private Long interviewId;
    private String questionText;
    private Integer questionOrder;
}

