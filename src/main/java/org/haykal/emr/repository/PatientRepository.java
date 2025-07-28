package org.haykal.emr.repository;

import org.haykal.emr.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<PatientRepository, UUID> {

    Optional<Patient> findByMrn(String mrn);

    boolean existsByMrn(String mrn);

    Page<Patient> findByFacilityId(UUID facilityId, Pageable pageable);

    @Query("SELECT p FROM Patient p WHERE " +
            "(:mrn IS NULL OR p.mrn = :mrn) AND " +
            "(:firstName IS NULL OR LOWER(p.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND " +
            "(:lastName IS NULL OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND " +
            "(:facilityId IS NULL OR p.facility.id = :facilityId) AND " +
            "(:serviceType IS NULL OR EXISTS (SELECT s FROM p.services s WHERE s.type = :serviceType))")
    Page<Patient> searchPatients(
            @Param("mrn") String mrn,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("facilityId") Long facilityId,
            @Param("serviceType") String serviceType,
            Pageable pageable
    );

    @Query("SELECT COUNT(p) FROM Patient p WHERE p.facility.id = :facilityId")
    UUID countByFacilityId(@Param("facilityId") UUID facilityId);
}
