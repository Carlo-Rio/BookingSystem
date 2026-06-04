package com.booking.system.v1.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

//Na razie jest to szkic, ale myślę, że pójdziemy w system rezerwujący Sale


//Klasa reservation posiada: id,nazwę, który użytkownik złożył requesta na resource,
// kiedy została złożona rezerwacja, dany resource
// Wiele userów może złożyć rezerwację
// ale każda rezerwacja dotyczy jednego zasobu i jednego usera
//Zrobimy jako szytwne godziny np. sala jest zajęta od 12 do 14, zamiast timera
//Będą to sloty czasowe, w których użytkownik chce zarezerwować sale

@Data
@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_reservation_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "resource_reservation_id")
    private Resource resource;

    @Column(name = "startTime")
    private LocalDateTime startTime;

    @Column(name = "endTime")
    private LocalDateTime endTime;


    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_status")
    private ReservationStatus reservationStatus = ReservationStatus.PENDING;


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();


    }


}
