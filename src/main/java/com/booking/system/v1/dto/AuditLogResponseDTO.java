package com.booking.system.v1.dto;

import com.booking.system.v1.entity.AuditAction;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuditLogResponseDTO {

    private Long id;

    private AuditAction action;

    private String performedBy;

    private String targetEntity;

    private Long targetId;

    private String description;

    private LocalDateTime createdAt;

}
