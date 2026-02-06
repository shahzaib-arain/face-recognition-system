package com.zaiby.facerecognition.repository;

import com.zaiby.facerecognition.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<Alert, Long> {
}
