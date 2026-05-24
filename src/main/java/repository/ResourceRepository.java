package repository;

import entity.Location;
import entity.ReservationStatus;
import entity.Resource;
import entity.ResourceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource, Long> {


    void findByLocation(String location);

    void findByStatus(ResourceStatus status);


//    @Query("SELECT r " +
//            "FROM Resource r " +
//            "WHERE r.capacity >= :capacity " +
//            "AND r.resourceStatus = :status ")
//    List<Resource> findByCapacityGreaterThan(
//            @Param("capacity") Integer capacity,
//            @Param("status") ResourceStatus status
//    );
//

    @Query("SELECT r " +
            "FROM Resource r " +
            "WHERE r.resourceStatus = entity.ResourceStatus.ACTIVE " +
            "AND r.location = :location")
    List<Resource> findByLocationAndStatus(
            @Param("location") Location location,
            @Param("status") ResourceStatus status
    );


    @Query("SELECT r " +
            "FROM Resource r " +
            "WHERE r.resourceStatus =  entity.ResourceStatus.ACTIVE " +
            "AND NOT EXISTS (" +
            "SELECT res FROM Reservation res " +
            "WHERE  res.resource = r " +
            "AND res.reservationStatus = entity.ReservationStatus.CONFIRMED " +
            "AND res.startTime < :end " +
            "AND res.endTime > :start)")
    List<Resource> findAvailableResources(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("activeStatus") ResourceStatus activeStatus,
            @Param("confirmedStatus") ReservationStatus confirmedStatus);


    List<Resource> findByCapacityGreaterThanEqualAndResourceStatus(int capacityIsGreaterThan, ResourceStatus resourceStatus);
}
