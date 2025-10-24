package br.gov.formosa.sigo2.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.br.CPF;

import java.util.UUID;

public class UserDTOs {

    public record AdminCreateUserDTO(
            @NotBlank
            String fullName,

            @NotBlank
            @Email
            String email,

            @NotBlank
            @CPF
            String cpf,

            @NotNull
            UUID roleId
    ) {}

    public record AdminRoleUpdateDTO(
            @NotNull
            UUID newRoleId
    ) {}

    public record OnboardingDataDTO(
            @NotBlank
            String identityDocumentFrontUrl,

            @NotBlank
            String identityDocumentBackUrl,

            @NotBlank
            String proofOfResidenceUrl
    ) {}

    public record UserResponseDTO(
            UUID id,
            String fullName,
            String email,
            String cpf,
            String roleName,
            boolean onboardingCompleted,
            String identityDocumentFrontUrl,
            String identityDocumentBackUrl,
            String proofOfResidenceUrl
    ) {}
}