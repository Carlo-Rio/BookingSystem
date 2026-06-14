package com.booking.system.v1.configuration;

import com.booking.system.v1.entity.Reservation;
import com.booking.system.v1.repository.ReservationRepository;
import com.booking.system.v1.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationReminderScheduler {

    private final ReservationRepository reservationRepository;

    private final EmailService emailService;


    @Value("${booking.reminder.hours-before}")
    private int hoursBefore;

    @Scheduled(fixedRate = 3600000) // runs every hour
    public void sendReminders() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminderTime = now.plusHours(hoursBefore);

        // find all confirmed reservations starting in the next X hours
        List<Reservation> upcoming = reservationRepository
                .findUpcomingReservations(now, reminderTime);

        for (Reservation reservation : upcoming) {
            try {
                emailService.sendReservationReminder(reservation);
                reservation.setReminderSent(true);
                reservationRepository.save(reservation);
            } catch (Exception e) {
                log.error("Failed to send reminder for reservation {}: {}", reservation.getId(), e.getMessage());
            }
        }
    }

}
