package com.booking.system.v1.mapper;

import com.booking.system.v1.dto.AvailableResourceFilterDTO;
import com.booking.system.v1.dto.ReservationResponseDTO;
import com.booking.system.v1.entity.Reservation;
import com.booking.system.v1.entity.ReservationStatus;
import com.booking.system.v1.entity.Resource;
import com.booking.system.v1.entity.User;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    public Reservation toEntity(AvailableResourceFilterDTO dto, User user, Resource resource)  {
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
