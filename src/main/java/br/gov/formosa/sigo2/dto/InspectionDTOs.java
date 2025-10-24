package br.gov.formosa.sigo2.dto;

import br.gov.formosa.sigo2.dto.RequestDTOs;
import br.gov.formosa.sigo2.model.enums.ChecklistItemType;
import br.gov.formosa.sigo2.model.enums.InspectionStatus;
import br.gov.formosa.sigo2.model.enums.InspectionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class InspectionDTOs {

    public record InspectionSummaryDTO(
            UUID inspectionId,
            UUID requestId,
            String requestProtocol,
            String requestTradeName,
            InspectionType type,
            InspectionStatus status
    ) {}

    public record ChecklistQuestionDTO(
            UUID templateItemId,
            String questionText,
            ChecklistItemType questionType,
            boolean required,
            String optionsJson
    ) {}

    public record InspectionDetailsDTO(
            UUID inspectionId,
            InspectionType type,
            InspectionStatus status,
            UUID requestId,
            String requestProtocol,
            String requestTradeName,
            RequestDTOs.LocationDTO requestLocation,
            List<ChecklistQuestionDTO> checklistQuestions
    ) {}

    public record SubmittedAnswerDTO(
            @NotNull
            UUID templateItemId,
            String answerValue,
            String observation
    ) {}

    public record SubmittedEvidenceDTO(
            @NotBlank
            String photoUrl
    ) {}

    public record SubmitInspectionDTO(
            @NotEmpty
            List<SubmittedAnswerDTO> answers,

            List<SubmittedEvidenceDTO> evidence,

            String observations,

            @NotNull
            @DecimalMin("0.0")
            BigDecimal calculatedFee
    ) {}

    public record SubmitCorrectionDTO(
            @NotEmpty
            List<SubmittedAnswerDTO> answers,

            List<SubmittedEvidenceDTO> evidence,

            @NotBlank
            String observations
    ) {}

    public record InspectionChecklistItemResponseDTO(
            UUID id,
            String questionText,
            String answerValue,
            String observation
    ) {}

    public record InspectionEvidenceResponseDTO(
            UUID id,
            String photoUrl
    ) {}

    public record FullInspectionResponseDTO(
            UUID id,
            InspectionType type,
            InspectionStatus status,
            LocalDateTime inspectionDate,
            BigDecimal calculatedFee,
            String observations,
            UUID requestId,
            String inspectorName,
            List<InspectionChecklistItemResponseDTO> inspectionItems,
            List<InspectionEvidenceResponseDTO> evidence
    ) {}
}