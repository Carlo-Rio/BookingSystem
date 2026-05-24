package repository;

import entity.Reservation;
import entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    //szukanie rezerwacji pomiędzy godzinami zaczęcia
    void findByStartTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    List<Reservation> findByUserId(Long userId);

    void findByResourceId(Long resourceId);

    void findByStatus(ReservationStatus status);

    boolean existsByResourceIdAndStartTimeAndEndTime(Long resourceId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(r) > 0 FROM Reservation r WHERE r.resource.id = :resourceId " +
            "AND r.startTime < :endTime " +
            "AND r.endTime > :startTime " +
            "AND r.reservationStatus = :status")
    boolean existsOverlappingReservation(
            @Param("resourceId") Long resourceId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("status") ReservationStatus status
    );

}
