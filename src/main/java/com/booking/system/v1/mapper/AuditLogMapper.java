package com.booking.system.v1.mapper;

import com.booking.system.v1.dto.AuditLogResponseDTO;
import com.booking.system.v1.entity.AuditLog;
import org.springframework.stereotype.Component;

@Component
public class AuditLogMapper {


    public AuditLogResponseDTO toResponseDTO(AuditLog auditLog) {
        AuditLogResponseDTO dto = new AuditLogResponseDTO();
        dto.setId(auditLog.getId());
        dto.setAction(auditLog.getAction());
        dto.setPerformedBy(auditLog.getPerformedBy());
        dto.setTargetEntity(auditLog.getTargetEntity());
        dto.setTargetId(auditLog.getTargetId());
        dto.setDescription(auditLog.getDescription());
        dto.setCreatedAt(auditLog.getCreatedAt());
        return dto;
    }

}
