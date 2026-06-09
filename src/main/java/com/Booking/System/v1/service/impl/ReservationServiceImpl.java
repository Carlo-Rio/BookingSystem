package com.booking.system.v1.service.impl;

import com.booking.system.v1.dto.AvailableResourceFilterDTO;
import com.booking.system.v1.dto.RescheduleReservationDTO;
import com.booking.system.v1.dto.ReservationResponseDTO;
import com.booking.system.v1.entity.*;
import com.booking.system.v1.exception.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import com.booking.system.v1.mapper.ReservationMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.booking.system.v1.repository.ReservationRepository;
import com.booking.system.v1.repository.ResourceRepository;
import com.booking.system.v1.repository.UserRepository;
import com.booking.system.v1.service.AuditLogService;
import com.booking.system.v1.service.ReservationService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    @Value("${booking.reservation.max-duration-hours}")
    private int maxDurationHours;

    @Value("${booking.reservation.min-duration-hours}")
    private int minDurationHours;


    //userId wants to reserve a specific resource
    // or will the system give the user an available
    // resource with filters that the user gave
    //check if userId exists
    //check if the resource exists
    //check if resource status is available

    @Transactional
    @Override
    public ReservationResponseDTO reserveResource(String email, AvailableResourceFilterDTO dto) {


        if (!dto.getStartTime().isBefore(dto.getEndTime())) {

            throw new TimeException("Start Time must be before End Time");
        }
        if (dto.getStartTime().isBefore(LocalDateTime.now())) {
            throw new TimeException("Cannot reserve in the past");
        }


        User user = userRepository.findByEmail(email).orElseThrow(()
                -> new UserNotFoundException("User not found"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UserNotActiveException("User not active");
        }

        // new duration check
        long durationInHours = ChronoUnit.HOURS.between(
                dto.getStartTime(),
                dto.getEndTime());

        if (durationInHours < minDurationHours) {
            throw new TimeException(
                    "Reservation must be at least " + minDurationHours + " hour(s)");
        }

        if (durationInHours > maxDurationHours) {
            throw new TimeException(
                    "Reservation cannot exceed " + maxDurationHours + " hours");
        }


        List<Resource> available = resourceRepository.findAvailableResourcesByFilters(
                dto.getStartTime(),
                dto.getEndTime(),
                dto.getLocation(),
                dto.getCapacity(),
                ResourceStatus.ACTIVE,
                ReservationStatus.CONFIRMED,
                ReservationStatus.PENDING
        );


        if (available.isEmpty()) {
            throw new NoRoomsAvailableException(
                    "No rooms available for " + dto.getLocation() +
                            " between " + dto.getStartTime() + " and " + dto.getEndTime() +
                            ". Please try a different time slot or location.");
        }

        Resource resource = available.stream()
                .min(Comparator.comparingInt(Resource::getCapacity))
                .orElseThrow(() -> new NoRoomsAvailableException("No rooms available"));



        Resource lockedResource = resourceRepository.findByIdWithLock(resource.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));



        boolean conflict = reservationRepository.existsOverlappingReservation(
                lockedResource.getId(),
                dto.getStartTime(),
                dto.getEndTime(),
                ReservationStatus.CONFIRMED,
                ReservationStatus.PENDING
        );

        if (conflict) {
            throw new OverlappingReservationException(
                    "Room was just booked. Please try again.");

        }






        Reservation reservation = reservationMapper.toEntity(dto, user, lockedResource);

        Reservation saved = reservationRepository.save(reservation);

        auditLogService.log(
                AuditAction.RESERVATION_CREATED,
                user.getUsername(),
                "Reservation",
                saved.getId(),
                "Reservation created for " + resource.getName()
        );

        return reservationMapper.toResponseDTO(saved);
    }

    @Transactional
    @Override
    public ReservationResponseDTO rescheduleReservation(Long reservationId, RescheduleReservationDTO dto) {


        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(()-> new ReservationNotFoundException("Reservation not found"));


        if (reservation.getReservationStatus() != ReservationStatus.CONFIRMED &&
                reservation.getReservationStatus() != ReservationStatus.PENDING) {
            throw new ReservationNotConfirmedException("Only active reservations can be rescheduled");
        }


        if (!dto.getNewStartTime().isBefore(dto.getNewEndTime())) {
            throw new TimeException("Start time must be before end time");
        }

        if (dto.getNewStartTime().isBefore(LocalDateTime.now())) {
            throw new TimeException("Cannot reschedule to a past time");
        }

        long durationInHours = ChronoUnit.HOURS.between(
                dto.getNewStartTime(),
                dto.getNewEndTime());

        if (durationInHours < minDurationHours) {
            throw new TimeException(
                    "Reservation must be at least " + minDurationHours + " hour(s)");
        }

        if (durationInHours > maxDurationHours) {
            throw new TimeException(
                    "Reservation cannot exceed " + maxDurationHours + " hours");
        }


        boolean conflict = reservationRepository.existsConflictExcludingReservation(
                reservation.getResource().getId(),  // which resource
                reservation.getId(),                // exclude this reservation from check
                dto.getNewStartTime(),
                dto.getNewEndTime(),
                ReservationStatus.CONFIRMED,
                ReservationStatus.PENDING
        );


        if (conflict) {
            throw new OverlappingReservationException("Selected time slot is unavailable");
        }

        reservation.setStartTime(dto.getNewStartTime());
        reservation.setEndTime(dto.getNewEndTime());
        Reservation saved = reservationRepository.save(reservation);


        auditLogService.log(
                AuditAction.RESERVATION_RESCHEDULED,
                reservation.getUser().getUsername(),
                "Reservation",
                reservationId,
                "Reservation rescheduled to " + dto.getNewStartTime() + " - " + dto.getNewEndTime()
        );

        return reservationMapper.toResponseDTO(saved);
    }

    @Transactional
    @Override
    public void cancelReservation(Long reservationId, String email) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));

        if (reservation.getReservationStatus() == ReservationStatus.CANCELLED) {

            throw new ReservationCancelledException("Reservation is already cancelled");

        }


        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));


        if (!reservation.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You cannot cancel this reservation");
        }


        reservation.setReservationStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        auditLogService.log(
                AuditAction.RESERVATION_CANCELLED,
                user.getUsername(),
                "Reservation",
                reservationId,
                "Reservation cancelled by user " + user.getUsername()
        );

    }

    @Override
    public ReservationResponseDTO findById(Long id) {

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));

        return reservationMapper.toResponseDTO(reservation);
    }

    @Override
    public Page<ReservationResponseDTO> findAll(Pageable pageable) {
        Page<Reservation> reservations = reservationRepository.findAll(pageable);

        return reservations.map(reservationMapper::toResponseDTO);
    }


    @Override
    public Page<ReservationResponseDTO> findByUserId(Long userId, Pageable pageable) {
        Page<Reservation> reservations = reservationRepository.findByUser_Id(userId, pageable);

        return reservations.map(reservationMapper::toResponseDTO);
    }

    @Override
    public void confirm(Long id) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));

        if (reservation.getReservationStatus() == ReservationStatus.CONFIRMED) {
            throw new ReservationConfirmedException("Reservation is already confirmed");
        }

            reservation.setReservationStatus(ReservationStatus.CONFIRMED);

        reservationRepository.save(reservation);
        auditLogService.log(
                AuditAction.RESERVATION_CONFIRMED,
                "ADMIN",
                "Reservation",
                id,
                "Reservation confirmed by admin"
        );
    }

    @Override
    public void cancel(Long id) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));
        if (reservation.getReservationStatus() == ReservationStatus.CANCELLED) {
            throw new ReservationCancelledException("Reservation is already cancelled");
        }
        reservation.setReservationStatus(ReservationStatus.CANCELLED);


        reservationRepository.save(reservation);
        auditLogService.log(
                AuditAction.RESERVATION_CANCELLED,
                "ADMIN",
                "Reservation",
                id,
                "Reservation cancelled by admin"
        );

    }


    public Page<ReservationResponseDTO> viewMyReservations(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.getStatus().equals(UserStatus.ACTIVE)) {
            throw new UserNotActiveException("User not active");
        }

        Page<Reservation> reservations = reservationRepository.findByUser_Id(user.getId(), pageable);

        return reservations.map(reservationMapper::toResponseDTO);
    }

}
