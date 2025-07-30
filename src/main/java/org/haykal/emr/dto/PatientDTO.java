package org.haykal.emr.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.Set;

@Data
public class PatientDTO {
        private Long id;
        private String mrn;

        @NotBlank(message = "First name is required")
        private String firstName;

        @NotBlank(message = "Last name is required")
        private String lastName;

        @NotNull(message = "Gender is required")
        private String gender;

        @NotNull(message = "Date of birth is required")
        @Past(message = "Date of birth must be in the past")
        private LocalDate dateOfBirth;

        @Email(message = "Invalid email format")
        private String email;

        @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number")
        private String phone;

        private String address;
        private String city;
        private String state;
        private String zipCode;

        private String insuranceProvider;
        private String insurancePolicyNumber;
        private String insuranceGroupNumber;

        @NotNull(message = "Facility ID is required")
        private Long facilityId;

        private String facilityName;
        private Set<ServiceRequestDTO> requestedServices;
}