
package com.interviewAi.Backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InterviewRequest {

    @NotBlank(message = "Interview title is required")
    private String title;

    private String topic;

    private String jobDescription;
}

