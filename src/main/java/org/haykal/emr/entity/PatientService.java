package org.haykal.emr.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "services", indexes = {
        @Index(name = "idx_service_name", columnList = "name"),
        @Index(name = "idx_service_type", columnList = "type"),
        @Index(name = "idx_service_facility", columnList = "facility_id")
})
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PatientService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_type_id", nullable = false)
    private ServiceType serviceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceStatus status = ServiceStatus.SCHEDULED;

    private LocalDateTime scheduledDate;
    private LocalDateTime completedDate;
    private String notes;
    private String providerName;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
}