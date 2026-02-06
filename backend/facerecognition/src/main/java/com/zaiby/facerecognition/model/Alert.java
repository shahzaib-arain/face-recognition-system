package com.zaiby.facerecognition.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alertId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;   // nullable → unknown face may not have user

    private String cameraId;
    private String alertType; // UNKNOWN_FACE / BLACKLIST / ACCESS_DENIED

    private LocalDateTime timestamp;

    @PrePersist
    public void prePersist() {
        timestamp = LocalDateTime.now();
    }
}
