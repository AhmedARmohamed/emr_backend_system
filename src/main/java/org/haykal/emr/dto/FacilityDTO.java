package org.haykal.emr.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.Set;

@Data
public class FacilityDTO {
        private Long id;

        @NotBlank(message = "Facility code is required")
        private String code;

        @NotBlank(message = "Facility name is required")
        private String name;

        private String address;
        private String city;
        private String state;
        private String zipCode;
        private String phone;
        private String email;
        private boolean active;
        private Set<ServiceTypeDTO> availableServices;
}
