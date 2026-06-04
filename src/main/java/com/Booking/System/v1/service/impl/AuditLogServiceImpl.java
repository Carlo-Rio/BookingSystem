package com.booking.system.v1.service.impl;

import com.booking.system.v1.dto.AuditLogResponseDTO;
import com.booking.system.v1.entity.AuditAction;
import com.booking.system.v1.entity.AuditLog;
import com.booking.system.v1.exception.AuditLogNotFoundException;
import lombok.RequiredArgsConstructor;
import com.booking.system.v1.mapper.AuditLogMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.booking.system.v1.repository.AuditLogRepository;
import com.booking.system.v1.service.AuditLogService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;

    @Override
    public void log(AuditAction action, String performedBy, String targetEntity, Long targetId, String description) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setPerformedBy(performedBy);
        log.setTargetEntity(targetEntity);
        log.setTargetId(targetId);
        log.setDescription(description);
        auditLogRepository.save(log);
    }

    @Override
    public Page<AuditLogResponseDTO> findAll(Pageable pageable) {
        Page<AuditLog> auditLog = auditLogRepository.findAll(pageable);

        return auditLog.map(auditLogMapper::toResponseDTO);
    }

    @Override
    public AuditLogResponseDTO findByAuditId(Long id) {

        AuditLog auditLog = auditLogRepository.findById(id).orElseThrow(() -> new AuditLogNotFoundException("Audit log not found"));

        return auditLogMapper.toResponseDTO(auditLog);
    }

    @Override
    public Page<AuditLogResponseDTO> findByAction(AuditAction action, Pageable pageable) {

        Page<AuditLog> auditLogs = auditLogRepository.findAuditLogsByAction(action, pageable);

        return auditLogs.map(auditLogMapper::toResponseDTO);
    }
}


