package com.booking.system.v1.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
@Data
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private AuditAction action;

    @Column(name = "performed_by", nullable = false)
    private String performedBy; // username of who triggered the action

    @Column(name = "target_entity", nullable = false)
    private String targetEntity; // e.g. "Reservation", "User", "Resource"

    @Column(name = "target_id", nullable = false)
    private Long targetId; // ID of the affected record

    @Column(name = "description")
    private String description; // human readable detail e.g. "Reservation #5 cancelled"

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }



}
