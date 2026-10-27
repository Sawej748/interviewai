
package com.interviewAi.Backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "interview_answers")
@Getter
@Setter
@NoArgsConstructor
public class InterviewAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Student ka submitted answer
    @Column(nullable = false, columnDefinition = "TEXT")
    private String answerText;

    // AI evaluation score: 0 se 100
    private Integer score;

    // AI ka feedback
    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Column(columnDefinition = "TEXT")
    private String strengths;

    @Column(columnDefinition = "TEXT")
    private String improvements;

    @Column(nullable = false, updatable = false)
    private LocalDateTime submittedAt;

    // Har answer ek interview question se linked hai
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private InterviewQuestion question;

    @PrePersist
    public void setSubmittedAt() {
        submittedAt = LocalDateTime.now();
    }
}
