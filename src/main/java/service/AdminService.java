package service;

import dto.*;
import entity.*;

import java.util.List;


// co admin może
// wyświetlenie listy użytkowników
// szukanie po użytkownika konkretnym id
// aktywowanie konta
// blokowanie konta
// usuwanie konta
// szukanie po username
// zmiana statusu

public interface AdminService {

    List<UserResponseDTO> findAll();

    UserResponseDTO findById(Long id);

    UserResponseDTO findByUsername(String username);

    void blockUser(Long id);

    void activateUser(Long id);

    void deleteUser(Long id);


    ReservationRequestDTO createResource(ReservationRequestDTO dto);

    ResourceResponseDTO editResource(Long id, ResourceRequestDTO dto);

    List<ResourceResponseDTO> findAllResources();

    void activateResource(Long id);

    void deleteResource(Long id);

    void deactivateResource(Long id);


    List<ReservationResponseDTO> findAllReservations();

    ReservationResponseDTO findReservationById(Long id);

    void confirmReservation(Long id);

    void cancelReservation(Long id);

    List<AuditLogResponseDTO> findAllAuditLogs();

    AuditLogResponseDTO findAuditLogById(Long id);

    List<AuditLogResponseDTO> findAuditLogsByActions(AuditAction action);


}
