package com.booking.system.v1.service;

import com.booking.system.v1.dto.AvailableResourceFilterDTO;
import com.booking.system.v1.dto.RescheduleReservationDTO;
import com.booking.system.v1.dto.ReservationResponseDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;




// reservation service
// user reserves a resource for given time
// reschedule a resource
// user cancels his reservation
// admin can find reservations by id
// admin can find reservations by userId
// admin can confirm or cancel incoming reservations




public interface ReservationService {

    ReservationResponseDTO reserveResource(String email,
                                           AvailableResourceFilterDTO dto);


    Page<ReservationResponseDTO> viewMyReservations(String email, Pageable pageable);



    ReservationResponseDTO rescheduleReservation(Long reservationId,
                                                 RescheduleReservationDTO dto);

    void cancelReservation(Long reservationId, String email);


    ReservationResponseDTO findById(Long id);

    Page<ReservationResponseDTO> findAll(Pageable pageable);


    Page<ReservationResponseDTO> findByUserId(Long userId, Pageable pageable);

    void confirm(Long id);
    void cancel(Long id);


}
