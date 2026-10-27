
package com.interviewAi.Backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "interview_reports")
@Getter
@Setter
@NoArgsConstructor
public class InterviewReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer totalQuestions;

    @Column(nullable = false)
    private Integer answeredQuestions;

    @Column(nullable = false)
    private Double averageScore;

    @Column(columnDefinition = "TEXT")
    private String strengths;

    @Column(columnDefinition = "TEXT")
    private String improvements;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Ek interview ki ek summary report
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id", nullable = false, unique = true)
    private Interview interview;

    @PrePersist
    @PreUpdate
    public void updateTime() {
        updatedAt = LocalDateTime.now();
    }
}

