package com.booking.system.v1.repository;

import com.booking.system.v1.entity.Reservation;
import com.booking.system.v1.entity.ReservationStatus;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {


    Page<Reservation> findByUser_Id(Long userId, Pageable pageable);

    List<Reservation> findByUser_Id(Long userId);


    List<Reservation> findByResource_Id(Long resourceId);


    Page<Reservation> findAll(@NonNull Pageable pageable);

    @Query("""
                SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
                FROM Reservation r
                WHERE r.resource.id = :resourceId
                  AND r.reservationStatus = :status
                  AND r.startTime < :end
                  AND r.endTime > :start
            """)
    boolean existsConflict(
            @Param("resourceId") Long resourceId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("status") ReservationStatus status
    );

    @Query("SELECT COUNT(r) > 0 FROM Reservation r " +
            "WHERE r.resource.id = :resourceId " +
            "AND r.startTime < :endTime " +
            "AND r.endTime > :startTime " +
            "AND (r.reservationStatus = :confirmedStatus " +
            "OR r.reservationStatus = :pendingStatus)")
    boolean existsOverlappingReservation(
            @Param("resourceId") Long resourceId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("confirmedStatus") ReservationStatus confirmedStatus);


    @Query("SELECT COUNT(r) > 0 FROM Reservation r " +
            "WHERE r.resource.id = :resourceId " +
            "AND r.id != :reservationId " +
            "AND r.startTime < :endTime " +
            "AND r.endTime > :startTime " +
            "AND (r.reservationStatus = :confirmedStatus " +
            "OR r.reservationStatus = :pendingStatus)")
    boolean existsConflictExcludingReservation(
            @Param("resourceId") Long resourceId,
            @Param("reservationId") Long reservationId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("confirmedStatus") ReservationStatus confirmedStatus,
            @Param("pendingStatus") ReservationStatus pendingStatus);


    @Query("SELECT r FROM Reservation r " +
            "WHERE r.reservationStatus = 'CONFIRMED'" +
            "AND r.startTime > :now " +
            "AND r.startTime <= :reminderTime " +
            "AND r.reminderSent = false")
    List<Reservation> findUpcomingReservations(
            @Param("now") LocalDateTime now,
            @Param("reminderTime") LocalDateTime reminderTime);


}
