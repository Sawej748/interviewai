
package com.interviewAi.Backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerateQuestionsRequest {

    // Kitne questions generate karne hain
    @Min(value = 1, message = "Minimum 1 question required")
    @Max(value = 20, message = "Maximum 20 questions allowed")
    private int questionCount = 10;
}

