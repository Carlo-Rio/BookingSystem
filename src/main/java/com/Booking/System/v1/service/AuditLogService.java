package com.booking.system.v1.service;

import com.booking.system.v1.dto.AuditLogResponseDTO;
import com.booking.system.v1.entity.AuditAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;




// audit logs
// find all audits
// find audit by id
// find audit by an action


public interface AuditLogService {
    void log(AuditAction action,String performedBy ,String targetEntity, Long targetId, String description);

    Page<AuditLogResponseDTO> findAll(Pageable pageable);

    AuditLogResponseDTO findByAuditId(Long id);

    Page<AuditLogResponseDTO> findByAction(AuditAction action, Pageable pageable);
}
