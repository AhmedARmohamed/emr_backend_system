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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FacilityService {
    private final FacilityRepository facilityRepository;
    private final ServiceTypeRepository serviceTypeRepository;

    public FacilityDTO createFacility(FacilityDTO dto) {
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
        return toDTO(facility);
    }

    public FacilityDTO getFacility(Long id) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facility not found"));
        return toDTO(facility);
    }

    public List<FacilityDTO> getAllActiveFacilities() {
        return facilityRepository.findByActiveTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public FacilityDTO addServiceToFacility(Long facilityId, Long serviceTypeId) {
        Facility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Facility not found"));

        ServiceType serviceType = serviceTypeRepository.findById(serviceTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Service type not found"));

        facility.getAvailableServices().add(serviceType);
        facility = facilityRepository.save(facility);

        return toDTO(facility);
    }

    private FacilityDTO toDTO(Facility facility) {
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

        if (facility.getAvailableServices() != null) {
            Set<ServiceTypeDTO> services = facility.getAvailableServices().stream()
                    .map(this::toServiceTypeDTO)
                    .collect(Collectors.toSet());
            dto.setAvailableServices(services);
        }

        return dto;
    }

    private ServiceTypeDTO toServiceTypeDTO(ServiceType serviceType) {
        ServiceTypeDTO dto = new ServiceTypeDTO();
        dto.setId(serviceType.getId());
        dto.setCode(serviceType.getCode());
        dto.setName(serviceType.getName());
        dto.setDescription(serviceType.getDescription());
        dto.setCategory(serviceType.getCategory().name());
        return dto;
    }
}