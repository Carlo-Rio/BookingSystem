package com.booking.system.v1.repository;

import com.booking.system.v1.entity.Location;
import com.booking.system.v1.entity.ReservationStatus;
import com.booking.system.v1.entity.Resource;
import com.booking.system.v1.entity.ResourceStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ResourceRepository extends JpaRepository<Resource, Long> {


    Page<Resource> findByResourceStatus(ResourceStatus status, Pageable page);


    boolean existsByLocationAndRoomNumber(
            Location location,
            Integer roomNumber);


    @Query("SELECT r " +
            "FROM Resource r " +
            "WHERE r.location = :location " +
            "AND r.resourceStatus = :activeStatus ")
    List<Resource> findByLocationAndStatus(
            @Param("location") Location location,
            @Param("activeStatus") ResourceStatus activeStatus
    );


    @Query("SELECT r " +
            "FROM Resource r " +
            "WHERE r.resourceStatus = :activeStatus " +
            "AND NOT EXISTS (" +
            "SELECT res FROM Reservation res " +
            "WHERE res.resource = r " +
            "AND (res.reservationStatus = :confirmedStatus " +
            "OR res.reservationStatus = :pendingStatus) " +
            "AND res.startTime < :end " +
            "AND res.endTime > :start)")
    List<Resource> findAvailableResources(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("activeStatus") ResourceStatus activeStatus,
            @Param("confirmedStatus") ReservationStatus confirmedStatus,
            @Param("pendingStatus") ReservationStatus pendingStatus);


    @Query("SELECT r " +
            "FROM Resource r " +
            "WHERE r.resourceStatus = :activeStatus " +
            "AND r.location = :location " +
            "AND r.capacity >= :capacity " +
            "AND NOT EXISTS (" +
            "SELECT res FROM Reservation res " +
            "WHERE res.resource = r " +
            "AND (res.reservationStatus = :confirmedStatus " +
            "OR res.reservationStatus = :pendingStatus) " +
            "AND res.startTime < :end " +
            "AND res.endTime > :start)")
    List<Resource> findAvailableResourcesByFilters(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("location") Location location,
            @Param("capacity") Integer capacity,
            @Param("activeStatus") ResourceStatus activeStatus,
            @Param("confirmedStatus") ReservationStatus confirmedStatus,
            @Param("pendingStatus") ReservationStatus pendingStatus);



    List<Resource> findByCapacityGreaterThanEqualAndResourceStatus(Integer capacity, ResourceStatus resourceStatus);


    List<Resource> findByCapacityAndResourceStatus(Integer capacity, ResourceStatus resourceStatus);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Resource r WHERE r.id = :id")
    Optional<Resource> findByIdWithLock(@Param("id") Long id);



}
