package org.haykal.emr.repository;


import org.haykal.emr.entity.ServiceCategory;
import org.haykal.emr.entity.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceTypeRepository extends JpaRepository<ServiceType, Long> {
    Optional<ServiceType> findByCode(String code);

    List<ServiceType> findByCategory(ServiceCategory category);

    @Query("SELECT st FROM ServiceType st JOIN st.facilities f WHERE f.id = :facilityId")
    List<ServiceType> findByFacilityId(@Param("facilityId") Long facilityId);
}