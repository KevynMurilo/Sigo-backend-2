package br.gov.formosa.sigo2.controller;

import br.gov.formosa.sigo2.dto.TemplateDTOs;
import br.gov.formosa.sigo2.service.template.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/templates")
@RequiredArgsConstructor
public class AdminTemplateController {

    private final CreateChecklistTemplateUseCase createChecklistTemplateUseCase;
    private final ListChecklistTemplatesUseCase listChecklistTemplatesUseCase;
    private final GetChecklistTemplateDetailsUseCase getChecklistTemplateDetailsUseCase;
    private final UpdateChecklistTemplateUseCase updateChecklistTemplateUseCase;
    private final DeleteChecklistTemplateUseCase deleteChecklistTemplateUseCase;

    @PostMapping
    public ResponseEntity<TemplateDTOs.ChecklistTemplateDetailsDTO> createTemplate(
            @Valid @RequestBody TemplateDTOs.CreateChecklistTemplateDTO dto) {

        TemplateDTOs.ChecklistTemplateDetailsDTO createdTemplate = createChecklistTemplateUseCase.execute(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdTemplate.id())
                .toUri();

        return ResponseEntity.created(location).body(createdTemplate);
    }

    @GetMapping
    public ResponseEntity<Page<TemplateDTOs.ChecklistTemplateSummaryDTO>> listTemplates(
            @PageableDefault Pageable pageable) {

        Page<TemplateDTOs.ChecklistTemplateSummaryDTO> page = listChecklistTemplatesUseCase.execute(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TemplateDTOs.ChecklistTemplateDetailsDTO> getTemplateDetails(
            @PathVariable UUID id) {

        TemplateDTOs.ChecklistTemplateDetailsDTO template = getChecklistTemplateDetailsUseCase.execute(id);
        return ResponseEntity.ok(template);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TemplateDTOs.ChecklistTemplateDetailsDTO> updateTemplate(
            @PathVariable UUID id,
            @Valid @RequestBody TemplateDTOs.UpdateChecklistTemplateDTO dto) {

        TemplateDTOs.ChecklistTemplateDetailsDTO updatedTemplate = updateChecklistTemplateUseCase.execute(id, dto);
        return ResponseEntity.ok(updatedTemplate);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable UUID id) {
        deleteChecklistTemplateUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}