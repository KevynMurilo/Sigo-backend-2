package br.gov.formosa.sigo2.dto;

import br.gov.formosa.sigo2.model.enums.OwnerType;
import br.gov.formosa.sigo2.model.enums.RequestStatus;
import br.gov.formosa.sigo2.model.enums.InspectionType;
import br.gov.formosa.sigo2.model.enums.PaymentStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class RequestDTOs {

    public record CreateNewRequestDTO(
            @NotNull
            OwnerType ownerType,

            String ownerDocument,
            String companyName,

            @NotBlank
            String tradeName,

            @NotBlank
            String commerceType,

            @NotNull
            @DecimalMin("0.1")
            BigDecimal latitude,

            @NotNull
            @DecimalMin("0.1")
            BigDecimal longitude,

            @NotNull
            @DecimalMin("0.1")
            BigDecimal areaWidth,

            @NotNull
            @DecimalMin("0.1")
            BigDecimal areaLength,

            @NotBlank
            String photoFrontUrl,
            @NotBlank
            String photoLeftUrl,
            @NotBlank
            String photoRightUrl,
            @NotBlank
            String photoBackUrl,

            String cnpjDocumentUrl
    ) {}

    public record SubmitCorrectionResponseDTO(
            @NotBlank
            String confirmationNotes,

            String newPhotoFrontUrl,
            String newPhotoLeftUrl,
            String newPhotoRightUrl,
            String newPhotoBackUrl,
            String newCnpjDocumentUrl
    ) {}

    public record TriageRequestDTO(
            @NotNull
            UUID fiscalUserId,
            UUID sanitaryUserId
    ) {}

    public record LocationDTO(
            BigDecimal latitude,
            BigDecimal longitude
    ) {}

    public record PhotosDTO(
            String photoFrontUrl,
            String photoLeftUrl,
            String photoRightUrl,
            String photoBackUrl,
            String cnpjDocumentUrl
    ) {}

    public record InspectionSummaryDTO(
            UUID id,
            InspectionType type,
            String inspectorName,
            LocalDateTime inspectionDate,
            br.gov.formosa.sigo2.model.enums.InspectionStatus status
    ) {}

    public record PaymentSummaryDTO(
            UUID id,
            BigDecimal totalAmount,
            LocalDate dueDate,
            PaymentStatus status,
            String billUrl
    ) {}

    public record StatusHistoryDTO(
            LocalDateTime timestamp,
            RequestStatus statusTo,
            String responsibleUserName
    ) {}

    public record RequestSummaryDTO(
            UUID id,
            String protocol,
            String tradeName,
            String ownerDocument,
            RequestStatus status,
            LocalDateTime createdAt
    ) {}

    public record RequestDetailsDTO(
            UUID id,
            String protocol,
            String applicantName,
            OwnerType ownerType,
            String ownerDocument,
            String companyName,
            String tradeName,
            String commerceType,
            BigDecimal areaSqM,
            RequestStatus status,
            LocalDateTime createdAt,
            LocalDate expiresAt,
            UUID parentRequestId,
            LocationDTO location,
            PhotosDTO photos,
            List<InspectionSummaryDTO> inspections,
            List<PaymentSummaryDTO> payments,
            List<StatusHistoryDTO> statusHistory
    ) {}
}