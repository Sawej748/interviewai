
package com.interviewAi.Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class InterviewResponse {

    private Long id;
    private String title;
    private String topic;
    private String status;
    private LocalDateTime createdAt;
}

