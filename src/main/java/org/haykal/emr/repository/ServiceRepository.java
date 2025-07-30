package org.haykal.emr.repository;

import org.haykal.emr.entity.PatientService;
import org.haykal.emr.entity.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServiceRepository extends JpaRepository<PatientService, UUID> {


    List<PatientService> findByFacilityId(UUID facilityId);

    List<PatientService> findByType(ServiceType type);

    @Query("SELECT s FROM PatientService s WHERE s.facility.id = :facilityId AND s.type = :type")
    List<PatientService> findByFacilityIdAndType(@Param("facilityId") UUID facilityId,
                                                 @Param("type") ServiceType type);
}
