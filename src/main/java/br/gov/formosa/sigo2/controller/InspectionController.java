package br.gov.formosa.sigo2.controller;

import br.gov.formosa.sigo2.dto.InspectionDTOs;
import br.gov.formosa.sigo2.model.User;
import br.gov.formosa.sigo2.service.inspection.GetInspectionDetailsUseCase;
import br.gov.formosa.sigo2.service.inspection.ListMyPendingInspectionsUseCase;
import br.gov.formosa.sigo2.service.inspection.SubmitInspectionApprovalUseCase;
import br.gov.formosa.sigo2.service.inspection.SubmitInspectionCorrectionUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/inspections")
@RequiredArgsConstructor
public class InspectionController {

    private final ListMyPendingInspectionsUseCase listMyPendingInspectionsUseCase;
    private final GetInspectionDetailsUseCase getInspectionDetailsUseCase;
    private final SubmitInspectionApprovalUseCase submitInspectionApprovalUseCase;
    private final SubmitInspectionCorrectionUseCase submitInspectionCorrectionUseCase;

    @GetMapping("/my-pending")
    public ResponseEntity<Page<InspectionDTOs.InspectionSummaryDTO>> getMyPendingInspections(
            @AuthenticationPrincipal User inspector,
            @PageableDefault Pageable pageable) {

        Page<InspectionDTOs.InspectionSummaryDTO> page = listMyPendingInspectionsUseCase.execute(inspector, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InspectionDTOs.InspectionDetailsDTO> getInspectionDetails(
            @PathVariable UUID id,
            @AuthenticationPrincipal User inspector) {

        InspectionDTOs.InspectionDetailsDTO details = getInspectionDetailsUseCase.execute(id, inspector);
        return ResponseEntity.ok(details);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<InspectionDTOs.FullInspectionResponseDTO> submitApproval(
            @PathVariable UUID id,
            @Valid @RequestBody InspectionDTOs.SubmitInspectionDTO dto,
            @AuthenticationPrincipal User inspector) {

        InspectionDTOs.FullInspectionResponseDTO response = submitInspectionApprovalUseCase.execute(id, dto, inspector);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/request-correction")
    public ResponseEntity<InspectionDTOs.FullInspectionResponseDTO> submitCorrection(
            @PathVariable UUID id,
            @Valid @RequestBody InspectionDTOs.SubmitCorrectionDTO dto,
            @AuthenticationPrincipal User inspector) {

        InspectionDTOs.FullInspectionResponseDTO response = submitInspectionCorrectionUseCase.execute(id, dto, inspector);
        return ResponseEntity.ok(response);
    }
}