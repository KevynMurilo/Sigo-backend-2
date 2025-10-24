package br.gov.formosa.sigo2.controller;

import br.gov.formosa.sigo2.dto.ReportDTOs;
import br.gov.formosa.sigo2.model.User;
import br.gov.formosa.sigo2.service.report.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final CreateReportUseCase createReportUseCase;
    private final ListReportsForTriageUseCase listReportsForTriageUseCase;
    private final AssignReportUseCase assignReportUseCase;
    private final ListMyPendingReportsUseCase listMyPendingReportsUseCase;
    private final ResolveReportUseCase resolveReportUseCase;
    private final GetReportDetailsUseCase getReportDetailsUseCase;

    @PostMapping
    public ResponseEntity<ReportDTOs.ReportDetailsDTO> createReport(
            @Valid @RequestBody ReportDTOs.CreateReportDTO dto,
            @AuthenticationPrincipal User currentUser) {

        ReportDTOs.ReportDetailsDTO report = createReportUseCase.execute(dto, currentUser);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(report.id())
                .toUri();

        return ResponseEntity.created(location).body(report);
    }

    @GetMapping("/triage")
    public ResponseEntity<Page<ReportDTOs.ReportSummaryDTO>> listReportsForTriage(
            @PageableDefault Pageable pageable) {

        Page<ReportDTOs.ReportSummaryDTO> page = listReportsForTriageUseCase.execute(pageable);
        return ResponseEntity.ok(page);
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<ReportDTOs.ReportDetailsDTO> assignReport(
            @PathVariable UUID id,
            @Valid @RequestBody ReportDTOs.AssignReportDTO dto,
            @AuthenticationPrincipal User secretario) {

        ReportDTOs.ReportDetailsDTO report = assignReportUseCase.execute(id, dto, secretario);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/my-pending")
    public ResponseEntity<Page<ReportDTOs.ReportSummaryDTO>> listMyPendingReports(
            @AuthenticationPrincipal User inspector,
            @PageableDefault Pageable pageable) {

        Page<ReportDTOs.ReportSummaryDTO> page = listMyPendingReportsUseCase.execute(inspector, pageable);
        return ResponseEntity.ok(page);
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<ReportDTOs.ReportDetailsDTO> resolveReport(
            @PathVariable UUID id,
            @Valid @RequestBody ReportDTOs.ResolveReportDTO dto,
            @AuthenticationPrincipal User inspector) {

        ReportDTOs.ReportDetailsDTO report = resolveReportUseCase.execute(id, dto, inspector);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportDTOs.ReportDetailsDTO> getReportDetails(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {

        ReportDTOs.ReportDetailsDTO report = getReportDetailsUseCase.execute(id, currentUser);
        return ResponseEntity.ok(report);
    }
}