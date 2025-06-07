package com.hotelbookingapplication.palatin.repository;

import com.hotelbookingapplication.palatin.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}