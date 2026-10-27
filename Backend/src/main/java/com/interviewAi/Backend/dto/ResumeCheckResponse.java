package com.interviewAi.Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResumeCheckResponse {

    private Integer atsScore;
    private String summary;
    private List<String> matchingKeywords;
    private List<String> missingKeywords;
    private List<String> suggestions;
}
