package org.haykal.emr.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @NotBlank(message = "Service name is required")
    @Size(max = 100, message = "Service name must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType type;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    @ManyToMany(mappedBy = "services", fetch = FetchType.LAZY)
    private Set<Patient> patients = new HashSet<>();
}