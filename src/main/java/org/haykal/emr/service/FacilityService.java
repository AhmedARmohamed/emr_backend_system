package org.haykal.emr.service;

import org.haykal.emr.dto.FacilityDTO;
import org.haykal.emr.dto.ServiceTypeDTO;
import org.haykal.emr.entity.Facility;
import org.haykal.emr.entity.ServiceType;
import org.haykal.emr.repository.FacilityRepository;
import org.haykal.emr.repository.ServiceTypeRepository;
import org.haykal.emr.exception.ResourceNotFoundException;
import org.haykal.emr.exception.DuplicateResourceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FacilityService {
    private final FacilityRepository facilityRepository;
    private final ServiceTypeRepository serviceTypeRepository;

    public FacilityDTO createFacility(FacilityDTO dto) {
        log.info("Creating facility with code: {}", dto.getCode());

        if (facilityRepository.existsByCode(dto.getCode())) {
            throw new DuplicateResourceException("Facility with code " + dto.getCode() + " already exists");
        }

        Facility facility = Facility.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .address(dto.getAddress())
                .city(dto.getCity())
                .state(dto.getState())
                .zipCode(dto.getZipCode())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .active(true)
                .build();

        facility = facilityRepository.save(facility);
        log.info("Facility created successfully with ID: {}", facility.getId());
        return toDTO(facility);
    }

    @Transactional(readOnly = true)
    public FacilityDTO getFacility(Long id) {
        log.info("Fetching facility with ID: {}", id);
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facility not found"));
        return toDTO(facility);
    }

    @Transactional(readOnly = true)
    public List<FacilityDTO> getAllActiveFacilities() {
        log.info("Fetching all active facilities");

        try {
            List<Facility> facilities = facilityRepository.findByActiveTrue();
            log.info("Found {} active facilities", facilities.size());

            if (facilities.isEmpty()) {
                log.warn("No active facilities found in database");
                return Collections.emptyList();
            }

            List<FacilityDTO> result = facilities.stream()
                    .map(facility -> {
                        try {
                            return toDTO(facility);
                        } catch (Exception e) {
                            log.error("Error converting facility to DTO: {}", facility.getId(), e);
                            throw new RuntimeException("Error processing facility: " + facility.getId(), e);
                        }
                    })
                    .collect(Collectors.toList());

            log.info("Successfully converted {} facilities to DTOs", result.size());
            return result;

        } catch (Exception e) {
            log.error("Error fetching active facilities", e);
            throw new RuntimeException("Error fetching facilities: " + e.getMessage(), e);
        }
    }

    public FacilityDTO addServiceToFacility(Long facilityId, Long serviceTypeId) {
        log.info("Adding service {} to facility {}", serviceTypeId, facilityId);

        Facility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Facility not found"));

        ServiceType serviceType = serviceTypeRepository.findById(serviceTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Service type not found"));

        facility.getAvailableServices().add(serviceType);
        facility = facilityRepository.save(facility);

        log.info("Service added successfully to facility {}", facilityId);
        return toDTO(facility);
    }

    private FacilityDTO toDTO(Facility facility) {
        if (facility == null) {
            log.error("Attempted to convert null facility to DTO");
            throw new IllegalArgumentException("Facility cannot be null");
        }

        log.debug("Converting facility to DTO: ID={}, Code={}", facility.getId(), facility.getCode());

        FacilityDTO dto = new FacilityDTO();
        dto.setId(facility.getId());
        dto.setCode(facility.getCode());
        dto.setName(facility.getName());
        dto.setAddress(facility.getAddress());
        dto.setCity(facility.getCity());
        dto.setState(facility.getState());
        dto.setZipCode(facility.getZipCode());
        dto.setPhone(facility.getPhone());
        dto.setEmail(facility.getEmail());
        dto.setActive(facility.isActive());

        // Handle availableServices safely to avoid lazy loading issues
        try {
            if (facility.getAvailableServices() != null && !facility.getAvailableServices().isEmpty()) {
                Set<ServiceTypeDTO> services = facility.getAvailableServices().stream()
                        .map(serviceType -> {
                            if (serviceType == null) {
                                log.warn("Found null service type for facility {}", facility.getId());
                                return null;
                            }
                            return toServiceTypeDTO(serviceType);
                        })
                        .filter(serviceDTO -> serviceDTO != null) // Remove any null DTOs
                        .collect(Collectors.toSet());
                dto.setAvailableServices(services);
            } else {
                dto.setAvailableServices(Collections.emptySet());
            }
        } catch (Exception e) {
            log.warn("Error processing available services for facility {}, setting empty set", facility.getId(), e);
            dto.setAvailableServices(Collections.emptySet());
        }

        log.debug("Successfully converted facility to DTO: {}", dto.getCode());
        return dto;
    }

    private ServiceTypeDTO toServiceTypeDTO(ServiceType serviceType) {
        if (serviceType == null) {
            log.error("Attempted to convert null service type to DTO");
            return null;
        }

        try {
            ServiceTypeDTO dto = new ServiceTypeDTO();
            dto.setId(serviceType.getId());
            dto.setCode(serviceType.getCode());
            dto.setName(serviceType.getName());
            dto.setDescription(serviceType.getDescription());
            dto.setCategory(serviceType.getCategory() != null ? serviceType.getCategory().name() : null);
            return dto;
        } catch (Exception e) {
            log.error("Error converting service type to DTO: {}", serviceType.getId(), e);
            return null;
        }
    }
}