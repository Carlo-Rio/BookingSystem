package mapper;

import dto.AuditLogResponseDTO;
import entity.AuditLog;

public class AuditLogMapper {

    // to co tylko konieczne do exposowania
    public AuditLogResponseDTO toResponseDTO(AuditLog auditLog) {


        AuditLogResponseDTO dto = new AuditLogResponseDTO();

        dto.setPerformedBy(auditLog.getPerformedBy());
        dto.setAction(dto.getAction());
        dto.setCreatedAt(auditLog.getCreatedAt());

        return dto;
    }

}
