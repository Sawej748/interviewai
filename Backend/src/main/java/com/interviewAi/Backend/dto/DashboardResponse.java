package com.interviewAi.Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DashboardResponse {

    private String name;
    private String email;
    private String welcomeMessage;
}
