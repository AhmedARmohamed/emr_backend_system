package org.haykal.emr.service;

import org.haykal.emr.dto.*;
import org.haykal.emr.entity.*;
import org.haykal.emr.repository.*;
import org.haykal.emr.exception.ResourceNotFoundException;
import org.haykal.emr.exception.DuplicateResourceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashSet;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PatientManagementService {
    private final PatientRepository patientRepository;
    private final FacilityRepository facilityRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final PatientServiceRepository patientServiceRepository;

    public PatientDTO createPatient(PatientDTO dto) {
        log.info("Creating new patient with data: {}", dto);

        // Validate gender
        validateGender(dto.getGender());

        // Check if MRN already exists
        if (dto.getMrn() != null && patientRepository.existsByMrn(dto.getMrn())) {
            throw new DuplicateResourceException("Patient with MRN " + dto.getMrn() + " already exists");
        }

        // Get facility
        Facility facility = facilityRepository.findById(dto.getFacilityId())
                .orElseThrow(() -> new ResourceNotFoundException("Facility not found with ID: " + dto.getFacilityId()));

        // Generate MRN if not provided
        if (dto.getMrn() == null || dto.getMrn().trim().isEmpty()) {
            dto.setMrn(generateMRN(facility.getCode()));
        }

        // Create patient
        Patient patient = Patient.builder()
                .mrn(dto.getMrn().trim())
                .firstName(dto.getFirstName().trim())
                .lastName(dto.getLastName().trim())
                .gender(Gender.valueOf(dto.getGender().toUpperCase()))
                .dateOfBirth(dto.getDateOfBirth())
                .email(dto.getEmail() != null ? dto.getEmail().trim() : null)
                .phone(dto.getPhone() != null ? dto.getPhone().trim() : null)
                .address(dto.getAddress() != null ? dto.getAddress().trim() : null)
                .city(dto.getCity() != null ? dto.getCity().trim() : null)
                .state(dto.getState() != null ? dto.getState().trim() : null)
                .zipCode(dto.getZipCode() != null ? dto.getZipCode().trim() : null)
                .insuranceProvider(dto.getInsuranceProvider() != null ? dto.getInsuranceProvider().trim() : null)
                .insurancePolicyNumber(dto.getInsurancePolicyNumber() != null ? dto.getInsurancePolicyNumber().trim() : null)
                .insuranceGroupNumber(dto.getInsuranceGroupNumber() != null ? dto.getInsuranceGroupNumber().trim() : null)
                .facility(facility)
                .services(new HashSet<>())
                .build();

        patient = patientRepository.save(patient);
        log.info("Patient created successfully with ID: {} and MRN: {}", patient.getId(), patient.getMrn());

        // Create requested services
        if (dto.getRequestedServices() != null && !dto.getRequestedServices().isEmpty()) {
            log.info("Creating {} requested services for patient", dto.getRequestedServices().size());
            for (ServiceRequestDTO serviceRequest : dto.getRequestedServices()) {
                createPatientService(patient, serviceRequest, facility);
            }
        }

        return toDTO(patient);
    }

    public PatientDTO updatePatient(Long id, PatientDTO dto) {
        log.info("Updating patient with ID: {}", id);

        // Validate gender
        validateGender(dto.getGender());

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + id));

        // Update patient fields
        patient.setFirstName(dto.getFirstName().trim());
        patient.setLastName(dto.getLastName().trim());
        patient.setGender(Gender.valueOf(dto.getGender().toUpperCase()));
        patient.setDateOfBirth(dto.getDateOfBirth());
        patient.setEmail(dto.getEmail() != null ? dto.getEmail().trim() : null);
        patient.setPhone(dto.getPhone() != null ? dto.getPhone().trim() : null);
        patient.setAddress(dto.getAddress() != null ? dto.getAddress().trim() : null);
        patient.setCity(dto.getCity() != null ? dto.getCity().trim() : null);
        patient.setState(dto.getState() != null ? dto.getState().trim() : null);
        patient.setZipCode(dto.getZipCode() != null ? dto.getZipCode().trim() : null);
        patient.setInsuranceProvider(dto.getInsuranceProvider() != null ? dto.getInsuranceProvider().trim() : null);
        patient.setInsurancePolicyNumber(dto.getInsurancePolicyNumber() != null ? dto.getInsurancePolicyNumber().trim() : null);
        patient.setInsuranceGroupNumber(dto.getInsuranceGroupNumber() != null ? dto.getInsuranceGroupNumber().trim() : null);

        patient = patientRepository.save(patient);
        log.info("Patient updated successfully with ID: {}", patient.getId());

        return toDTO(patient);
    }

    @Transactional(readOnly = true)
    public PatientDTO getPatient(Long id) {
        log.debug("Fetching patient with ID: {}", id);
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + id));
        return toDTO(patient);
    }

    @Transactional(readOnly = true)
    public PatientDTO getPatientByMrn(String mrn) {
        log.debug("Fetching patient with MRN: {}", mrn);
        if (mrn == null || mrn.trim().isEmpty()) {
            throw new IllegalArgumentException("MRN cannot be null or empty");
        }

        Patient patient = patientRepository.findByMrn(mrn.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with MRN: " + mrn));
        return toDTO(patient);
    }

    @Transactional(readOnly = true)
    public PageResponse<PatientDTO> searchPatients(Long facilityId, String search, Pageable pageable) {
        log.debug("Searching patients for facility ID: {} with search term: {}", facilityId, search);

        if (facilityId == null) {
            throw new IllegalArgumentException("Facility ID cannot be null");
        }

        // Verify facility exists
        if (!facilityRepository.existsById(facilityId)) {
            throw new ResourceNotFoundException("Facility not found with ID: " + facilityId);
        }

        Page<Patient> patients;
        if (search != null && !search.trim().isEmpty()) {
            patients = patientRepository.searchByFacility(facilityId, search.trim(), pageable);
        } else {
            patients = patientRepository.findByFacilityId(facilityId, pageable);
        }

        Page<PatientDTO> dtoPage = patients.map(this::toDTO);
        return PageResponse.of(dtoPage);
    }

    public void deletePatient(Long id) {
        log.info("Deleting patient with ID: {}", id);

        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Patient not found with ID: " + id);
        }

        patientRepository.deleteById(id);
        log.info("Patient deleted successfully with ID: {}", id);
    }

    private void createPatientService(Patient patient, ServiceRequestDTO request, Facility facility) {
        log.debug("Creating patient service for patient ID: {} and service type ID: {}",
                patient.getId(), request.getServiceTypeId());

        ServiceType serviceType = serviceTypeRepository.findById(request.getServiceTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Service type not found with ID: " + request.getServiceTypeId()));

        PatientService patientService = PatientService.builder()
                .patient(patient)
                .serviceType(serviceType)
                .facility(facility)
                .scheduledDate(request.getScheduledDate() != null ?
                        request.getScheduledDate() : LocalDateTime.now().plusDays(1)) // Default to tomorrow
                .notes(request.getNotes() != null ? request.getNotes().trim() : null)
                .providerName(request.getProviderName() != null ? request.getProviderName().trim() : null)
                .status(ServiceStatus.SCHEDULED)
                .build();

        patientServiceRepository.save(patientService);
        log.debug("Patient service created successfully for patient ID: {}", patient.getId());
    }

    private String generateMRN(String facilityCode) {
        if (facilityCode == null || facilityCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Facility code cannot be null or empty for MRN generation");
        }

        String prefix = facilityCode.toUpperCase().trim();
        Integer maxNumber = patientRepository.findMaxMrnNumber(prefix, prefix.length());
        int nextNumber = (maxNumber != null && maxNumber > 0) ? maxNumber + 1 : 1000;
        String mrn = String.format("%s%06d", prefix, nextNumber);

        log.debug("Generated MRN: {} for facility code: {}", mrn, facilityCode);
        return mrn;
    }

    private void validateGender(String gender) {
        if (gender == null || gender.trim().isEmpty()) {
            throw new IllegalArgumentException("Gender cannot be null or empty");
        }

        try {
            Gender.valueOf(gender.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid gender value: " + gender +
                    ". Valid values are: MALE, FEMALE, OTHER");
        }
    }

    private PatientDTO toDTO(Patient patient) {
        PatientDTO dto = new PatientDTO();
        dto.setId(patient.getId());
        dto.setMrn(patient.getMrn());
        dto.setFirstName(patient.getFirstName());
        dto.setLastName(patient.getLastName());
        dto.setGender(patient.getGender().name());
        dto.setDateOfBirth(patient.getDateOfBirth());
        dto.setEmail(patient.getEmail());
        dto.setPhone(patient.getPhone());
        dto.setAddress(patient.getAddress());
        dto.setCity(patient.getCity());
        dto.setState(patient.getState());
        dto.setZipCode(patient.getZipCode());
        dto.setInsuranceProvider(patient.getInsuranceProvider());
        dto.setInsurancePolicyNumber(patient.getInsurancePolicyNumber());
        dto.setInsuranceGroupNumber(patient.getInsuranceGroupNumber());

        // Handle potential lazy loading issues
        if (patient.getFacility() != null) {
            dto.setFacilityId(patient.getFacility().getId());
            dto.setFacilityName(patient.getFacility().getName());
        }

        return dto;
    }
}