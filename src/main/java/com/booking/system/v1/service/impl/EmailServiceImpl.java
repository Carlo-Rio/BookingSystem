package com.booking.system.v1.service.impl;

import com.booking.system.v1.entity.Reservation;
import com.booking.system.v1.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    private final TemplateEngine templateEngine;


    @Value("${spring.mail.username}")
    private String username;

    @Override
    public void sendReservationConfirmation(Reservation reservation) {

        Context context = new Context();

        context.setVariable("username",
                reservation.getUser().getUsername());
        context.setVariable("resourceName",
                reservation.getResource().getName());
        context.setVariable("roomNumber",
                reservation.getResource().getRoomNumber());
        context.setVariable("startTime",
                reservation.getStartTime());
        context.setVariable("endTime",
                reservation.getEndTime());
        context.setVariable("location",
                reservation.getResource().getLocation());

        String html = templateEngine.process(
                "emails/confirmation", context);

        sendEmail(
                reservation.getUser().getEmail(),
                "Reservation Confirmed",
                html
        );

    }

    @Override
    public void sendReservationCancellation(Reservation reservation) {
        Context context = createContext();

        context.setVariable("username",
                reservation.getUser().getUsername());
        context.setVariable("resourceName",
                reservation.getResource().getName());
        context.setVariable("startTime",
                reservation.getStartTime());
        context.setVariable("location",
                reservation.getResource().getLocation());


        String html = templateEngine.process(
                "emails/cancellation", context);

        sendEmail(reservation.getUser().getEmail(),
                "Reservation Cancelled",
                html);
    }

    @Override
    public void sendReservationReminder(Reservation reservation) {

        Context context = createContext();

        context.setVariable("username",
                reservation.getUser().getUsername());
        context.setVariable("resourceName",
                reservation.getResource().getName());
        context.setVariable("startTime",
                reservation.getStartTime());
        context.setVariable("endTime",
                reservation.getEndTime());
        context.setVariable("location",
                reservation.getResource().getLocation());
        context.setVariable("roomNumber",
                reservation.getResource().getRoomNumber());


        String html = templateEngine.process(
                "emails/reminder",
                context
        );


        sendEmail(reservation.getUser().getEmail(),
                "Reminder: Upcoming Reservation",
                html);


    }

    private void sendEmail(String to, String subject, String html) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(
                    message, true, "UTF-8");
            helper.setTo(to);
            helper.setFrom(username);  // ← add this
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
            log.info("Email sent to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send email: "
                    + e.getMessage());
        }
    }

    private Context createContext() {
        return new Context();
    }

}
