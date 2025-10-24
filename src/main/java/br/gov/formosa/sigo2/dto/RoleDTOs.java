package br.gov.formosa.sigo2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public class RoleDTOs {

    public record RoleResponseDTO(
            UUID id,
            String name
    ) {}

    public record CreateRoleDTO(
            @NotBlank
            @Size(min = 3, max = 50)
            String name
    ) {}

    public record UpdateRoleDTO(
            @NotBlank
            @Size(min = 3, max = 50)
            String name
    ) {}
}