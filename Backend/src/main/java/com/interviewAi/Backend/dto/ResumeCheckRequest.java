package com.interviewAi.Backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResumeCheckRequest {

    @NotBlank(message = "Resume text is required")
    private String resumeText;

    private String jobDescription;
}
