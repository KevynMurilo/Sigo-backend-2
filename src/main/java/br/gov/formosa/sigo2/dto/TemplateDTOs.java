package br.gov.formosa.sigo2.dto;

import br.gov.formosa.sigo2.model.enums.ChecklistItemType;
import br.gov.formosa.sigo2.model.enums.InspectionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public class TemplateDTOs {

    public record ChecklistItemDTO(
            UUID id,
            @NotBlank
            String questionText,
            @NotNull
            ChecklistItemType questionType,
            @NotNull
            Integer itemOrder,
            @NotNull
            Boolean required,
            String optionsJson
    ) {}

    public record CreateChecklistItemDTO(
            @NotBlank
            String questionText,
            @NotNull
            ChecklistItemType questionType,
            @NotNull
            Integer itemOrder,
            @NotNull
            Boolean required,
            String optionsJson
    ) {}

    public record UpdateChecklistTemplateDTO(
            @NotBlank
            String name,
            @NotNull
            InspectionType type,
            @NotNull
            Boolean active,
            @Valid
            @NotEmpty
            List<CreateChecklistItemDTO> items
    ) {}

    public record CreateChecklistTemplateDTO(
            @NotBlank
            String name,
            @NotNull
            InspectionType type,
            @Valid
            @NotEmpty
            List<CreateChecklistItemDTO> items
    ) {}

    public record ChecklistTemplateSummaryDTO(
            UUID id,
            String name,
            InspectionType type,
            boolean active,
            int itemCount
    ) {}

    public record ChecklistTemplateDetailsDTO(
            UUID id,
            String name,
            InspectionType type,
            boolean active,
            List<ChecklistItemDTO> items
    ) {}
}