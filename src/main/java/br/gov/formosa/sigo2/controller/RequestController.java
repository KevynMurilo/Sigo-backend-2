package br.gov.formosa.sigo2.controller;

import br.gov.formosa.sigo2.dto.RequestDTOs;
import br.gov.formosa.sigo2.model.User;
import br.gov.formosa.sigo2.model.enums.RequestStatus;
import br.gov.formosa.sigo2.service.request.*;
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
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class RequestController {

    private final CreateNewRequestUseCase createNewRequestUseCase;
    private final ListMyRequestsUseCase listMyRequestsUseCase;
    private final FindRequestDetailsUseCase findRequestDetailsUseCase;
    private final ListAllRequestsUseCase listAllRequestsUseCase;
    private final TriageRequestUseCase triageRequestUseCase;
    private final HandleRenewalUseCase handleRenewalUseCase;
    private final SubmitCorrectionResponseUseCase submitCorrectionResponseUseCase;

    @PostMapping
    public ResponseEntity<RequestDTOs.RequestDetailsDTO> createNewRequest(
            @Valid @RequestBody RequestDTOs.CreateNewRequestDTO dto,
            @AuthenticationPrincipal User currentUser) {

        RequestDTOs.RequestDetailsDTO createdRequest = createNewRequestUseCase.execute(dto, currentUser);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdRequest.id())
                .toUri();

        return ResponseEntity.created(location).body(createdRequest);
    }

    @PostMapping("/{id}/submit-correction")
    public ResponseEntity<RequestDTOs.RequestDetailsDTO> submitCorrectionResponse(
            @PathVariable UUID id,
            @Valid @RequestBody RequestDTOs.SubmitCorrectionResponseDTO dto,
            @AuthenticationPrincipal User applicant) {

        RequestDTOs.RequestDetailsDTO updatedRequest = submitCorrectionResponseUseCase.execute(id, dto, applicant);
        return ResponseEntity.ok(updatedRequest);
    }

    @GetMapping("/my")
    public ResponseEntity<Page<RequestDTOs.RequestSummaryDTO>> listMyRequests(
            @AuthenticationPrincipal User currentUser,
            @PageableDefault Pageable pageable) {

        Page<RequestDTOs.RequestSummaryDTO> page = listMyRequestsUseCase.execute(currentUser, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping
    public ResponseEntity<Page<RequestDTOs.RequestSummaryDTO>> listAllRequests(
            @RequestParam(required = false) RequestStatus status,
            @PageableDefault Pageable pageable) {

        Page<RequestDTOs.RequestSummaryDTO> page = listAllRequestsUseCase.execute(status, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RequestDTOs.RequestDetailsDTO> getRequestDetails(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {

        RequestDTOs.RequestDetailsDTO details = findRequestDetailsUseCase.execute(id, currentUser);
        return ResponseEntity.ok(details);
    }

    @PostMapping("/{id}/triage")
    public ResponseEntity<RequestDTOs.RequestDetailsDTO> triageRequest(
            @PathVariable UUID id,
            @Valid @RequestBody RequestDTOs.TriageRequestDTO dto,
            @AuthenticationPrincipal User secretario) {

        RequestDTOs.RequestDetailsDTO triagedRequest = triageRequestUseCase.execute(id, dto, secretario);
        return ResponseEntity.ok(triagedRequest);
    }

    @PostMapping("/{id}/renewal/accept")
    public ResponseEntity<RequestDTOs.RequestDetailsDTO> acceptRenewal(
            @PathVariable UUID id,
            @AuthenticationPrincipal User applicant) {

        RequestDTOs.RequestDetailsDTO renewedRequest = handleRenewalUseCase.accept(id, applicant);
        return ResponseEntity.ok(renewedRequest);
    }

    @PostMapping("/{id}/renewal/reject")
    public ResponseEntity<RequestDTOs.RequestDetailsDTO> rejectRenewal(
            @PathVariable UUID id,
            @AuthenticationPrincipal User applicant) {

        RequestDTOs.RequestDetailsDTO rejectedRequest = handleRenewalUseCase.reject(id, applicant);
        return ResponseEntity.ok(rejectedRequest);
    }
}