package com.example.iam2.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
@Getter
@Setter
public class AuditLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String entityName;

    private Long entityId;

    private String action;

    private String changedBy;

    private LocalDateTime changedAt = LocalDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String details;
}
