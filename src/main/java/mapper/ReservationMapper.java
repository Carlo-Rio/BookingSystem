package mapper;

import dto.ReservationRequestDTO;
import dto.ReservationResponseDTO;
import entity.Reservation;
import entity.ReservationStatus;
import entity.Resource;
import entity.User;

public class ReservationMapper {

    public Reservation toEntity(ReservationRequestDTO dto, User user, Resource resource)  {
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setResource(resource);
        reservation.setStartTime(dto.getStartTime());
        reservation.setEndTime(dto.getEndTime());
        reservation.setReservationStatus(ReservationStatus.PENDING);

        return reservation;

    }

    public ReservationResponseDTO toResponseDTO(Reservation reservation) {

        ReservationResponseDTO dto = new ReservationResponseDTO();
        dto.setUsername(reservation.getUser().getUsername());
        dto.setResourceName(reservation.getResource().getName());
        dto.setLocation(reservation.getResource().getLocation());
        dto.setStartTime(reservation.getStartTime());
        dto.setEndTime(reservation.getEndTime());
        dto.setReservationId(reservation.getId());

        return dto;
    }

}
