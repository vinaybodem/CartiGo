package com.cartigo.auditloggingservice.repository;

import com.cartigo.auditloggingservice.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {}
