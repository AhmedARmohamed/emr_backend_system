package org.haykal.emr.dto;

import lombok.Data;

@Data
public class ServiceTypeDTO {
    private Long id;
    private String code;
    private String name;
    private String description;
    private String category;
}
