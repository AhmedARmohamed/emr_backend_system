package org.haykal.emr.mapper;

import org.haykal.emr.dto.PatientDto;
import org.haykal.emr.entity.Patient;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class PatientMapper {

    private final ServiceMapper serviceMapper;

    public PatientMapper(ServiceMapper serviceMapper) {
        this.serviceMapper = serviceMapper;
    }

    public PatientDto toDTO(Patient patient) {
        if (patient == null) {
            return null;
        }

        return new PatientDto(
                patient.getId(),
                patient.getMrn(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getGender(),
                patient.getDateOfBirth(),
                patient.getPhone(),
                patient.getEmail(),
                patient.getAddress(),
                patient.getInsuranceProvider(),
                patient.getInsuranceNumber(),
                patient.getFacility().getId(),
                patient.getServices().stream()
                        .map(serviceMapper::toDTO)
                        .collect(Collectors.toSet()),
                patient.getCreatedAt(),
                patient.getUpdatedAt()
        );
    }
}
