
package com.interviewAi.Backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitAnswerRequest {

    // Student ka submitted answer
    @NotBlank(message = "Answer cannot be empty")
    private String answerText;
}

