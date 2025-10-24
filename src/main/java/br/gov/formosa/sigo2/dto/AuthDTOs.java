package br.gov.formosa.sigo2.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public class AuthDTOs {

    public record LoginResponseDTO(
            UUID userId,
            String fullName,
            String email,
            String roleName,
            boolean onboardingCompleted,
            String token
    ) {}

    public record UserInfoDTO(
            String cpf,
            String fullName,
            String email
    ) {}

    public record SimulateLoginRequestDTO(
            @NotBlank
            String cpf,
            @NotBlank
            String fullName,
            @NotBlank
            @jakarta.validation.constraints.Email
            String email
    ) {}
}