package com.booking.system.v1.service;

import com.booking.system.v1.entity.Reservation;

public interface EmailService {

    void sendReservationConfirmation(Reservation reservation);

    void sendReservationCancellation(Reservation reservation);

    void sendReservationReminder(Reservation reservation);

}
