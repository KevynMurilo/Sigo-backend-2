package br.gov.formosa.sigo2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ConfigDTOs {

    public record ConfigurationDTO(
            @NotBlank
            @Size(max = 255)
            String key,

            @NotBlank
            String value
    ) {}
}