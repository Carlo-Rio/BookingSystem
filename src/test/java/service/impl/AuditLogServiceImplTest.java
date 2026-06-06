package service.impl;

import com.booking.system.v1.dto.AuditLogResponseDTO;
import com.booking.system.v1.entity.AuditAction;
import com.booking.system.v1.entity.AuditLog;
import com.booking.system.v1.exception.AuditLogNotFoundException;
import com.booking.system.v1.mapper.AuditLogMapper;
import com.booking.system.v1.service.impl.AuditLogServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.booking.system.v1.repository.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AuditLogServiceImplTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private AuditLogMapper auditLogMapper;

    @InjectMocks
    private AuditLogServiceImpl auditLogService;

    // ─── log() ─────────────────────────────────────────────────

    @Test
    void log_shouldSaveAuditLog_withCorrectFields() {

        // Act
        auditLogService.log(
                AuditAction.RESERVATION_CREATED,
                "johndoe",
                "Reservation",
                1L,
                "Reservation created"
        );

        // Assert
        verify(auditLogRepository).save(argThat(log ->
                log.getAction() == AuditAction.RESERVATION_CREATED &&
                        log.getPerformedBy().equals("johndoe") &&
                        log.getTargetEntity().equals("Reservation") &&
                        log.getTargetId().equals(1L) &&
                        log.getDescription().equals("Reservation created")
        ));
    }

    // ─── findByAuditId() ───────────────────────────────────────

    @Test
    void findByAuditId_shouldReturnDTO_whenAuditLogExists() {

        // Arrange
        AuditLog auditLog = new AuditLog();
        auditLog.setId(1L);

        AuditLogResponseDTO responseDTO = new AuditLogResponseDTO();
        responseDTO.setId(1L);

        when(auditLogRepository.findById(1L)).thenReturn(Optional.of(auditLog));
        when(auditLogMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        AuditLogResponseDTO result = auditLogService.findByAuditId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findByAuditId_shouldThrowAuditLogNotFoundException_whenNotFound() {

        // Arrange
        when(auditLogRepository.findById(99L)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(AuditLogNotFoundException.class,
                () -> auditLogService.findByAuditId(99L));

        verify(auditLogMapper, never()).toResponseDTO(any());
    }

    // ─── findByAction() ────────────────────────────────────────

    @Test
    void findByAction_shouldReturnFilteredLogs_whenActionExists() {

        // Arrange
        AuditLog auditLog = new AuditLog();
        Pageable pageable = PageRequest.of(0, 20);
        Page<AuditLog> auditLogPage = new PageImpl<>(
                List.of(auditLog),
                pageable,
                1);
        AuditLogResponseDTO responseDTO = new AuditLogResponseDTO();

        responseDTO.setAction(AuditAction.RESERVATION_CREATED);



        when(auditLogRepository.findAuditLogsByAction(AuditAction.RESERVATION_CREATED, pageable))
                .thenReturn(auditLogPage);
        when(auditLogMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        Page<AuditLogResponseDTO> result =
                auditLogService.findByAction(AuditAction.RESERVATION_CREATED, pageable);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getContent().size());
        assertEquals(AuditAction.RESERVATION_CREATED,
                result.getContent().get(0).getAction());
    }

    @Test
    void findByAction_shouldReturnEmptyPage_whenNoLogsExist() {

        Pageable pageable = PageRequest.of(0, 20);

        when(auditLogRepository.findAuditLogsByAction(
                AuditAction.RESERVATION_CREATED, pageable))
                .thenReturn(Page.empty(pageable));

        Page<AuditLogResponseDTO> result =
                auditLogService.findByAction(AuditAction.RESERVATION_CREATED, pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.getContent().size());
    }

}