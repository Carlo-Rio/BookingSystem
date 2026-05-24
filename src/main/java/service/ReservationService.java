package service;

import dto.RescheduleDTO;
import dto.ReservationRequestDTO;
import dto.ReservationResponseDTO;
import entity.Reservation;
import entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;


//użytkownik chce zarezerwować konkretny resourceId od godziny takiej do takiej
// trzeba sprawdzić czy dostępny jest dany resource id, czy te godziny są też wolne
// anulowanie rezerwacji
// reschedulowanie rezerwacji


public interface ReservationService {

    ReservationResponseDTO reserveResource(Long userId,
                                           ReservationRequestDTO dto);




    ReservationResponseDTO rescheduleReservation(Long reservationId,
                                                 RescheduleDTO dto);

    void cancelReservation(Long reservationId, Long userId);


    ReservationResponseDTO findById(Long id);

}
