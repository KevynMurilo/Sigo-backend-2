package br.gov.formosa.sigo2.dto;

import br.gov.formosa.sigo2.model.enums.ReportStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class ReportDTOs {

    public record CreateReportDTO(
            @NotNull
            @DecimalMin("0.1")
            BigDecimal latitude,

            @NotNull
            @DecimalMin("0.1")
            BigDecimal longitude,

            @NotBlank
            String description,

            List<String> evidenceFileUrls,

            @NotNull
            Boolean anonymous
    ) {}

    public record AssignReportDTO(
            @NotNull
            UUID assignToUserId
    ) {}

    public record ResolveReportDTO(
            @NotBlank
            String resultNotes
    ) {}

    public record ReportEvidenceDTO(
            UUID id,
            String fileUrl
    ) {}

    public record ReportSummaryDTO(
            UUID id,
            String protocol,
            ReportStatus status,
            String descriptionSnippet,
            String assignedToName
    ) {}

    public record ReportDetailsDTO(
            UUID id,
            String protocol,
            ReportStatus status,
            String description,
            BigDecimal latitude,
            BigDecimal longitude,
            boolean anonymous,
            String reporterName,
            String assignedToName,
            List<ReportEvidenceDTO> evidence
    ) {}
}