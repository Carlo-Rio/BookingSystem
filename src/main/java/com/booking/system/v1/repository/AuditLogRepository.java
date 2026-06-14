package com.booking.system.v1.repository;

import com.booking.system.v1.entity.AuditAction;
import com.booking.system.v1.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog,Long> {





    Page<AuditLog> findAuditLogsByAction(AuditAction action, Pageable pageable);

}
