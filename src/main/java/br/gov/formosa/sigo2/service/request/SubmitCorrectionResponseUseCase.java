package br.gov.formosa.sigo2.service.request;

import br.gov.formosa.sigo2.dto.RequestDTOs;
import br.gov.formosa.sigo2.mapper.RequestMapper;
import br.gov.formosa.sigo2.model.Inspection;
import br.gov.formosa.sigo2.model.Request;
import br.gov.formosa.sigo2.model.User;
import br.gov.formosa.sigo2.model.enums.InspectionStatus;
import br.gov.formosa.sigo2.model.enums.InspectionType;
import br.gov.formosa.sigo2.model.enums.RequestStatus;
import br.gov.formosa.sigo2.repository.InspectionRepository;
import br.gov.formosa.sigo2.repository.RequestRepository;
import br.gov.formosa.sigo2.service.util.StatusHistoryService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubmitCorrectionResponseUseCase {

    private final RequestRepository requestRepository;
    private final InspectionRepository inspectionRepository;
    private final StatusHistoryService statusHistoryService;
    private final RequestMapper requestMapper;

    @Transactional
    public RequestDTOs.RequestDetailsDTO execute(UUID requestId, RequestDTOs.SubmitCorrectionResponseDTO dto, User applicant) {

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação não encontrada: " + requestId));

        if (!request.getApplicant().equals(applicant)) {
            throw new AccessDeniedException("Usuário não é o solicitante desta requisição.");
        }

        if (request.getStatus() != RequestStatus.AGUARDANDO_CORRECAO) {
            throw new ValidationException("Esta solicitação não está aguardando correção.");
        }

        Inspection sanitaryInspection = inspectionRepository.findByRequestAndTypeAndStatus(
                        request, InspectionType.SANITARIA, InspectionStatus.CORRECAO)
                .orElseThrow(() -> new IllegalStateException("Vistoria sanitária em status de correção não encontrada para esta solicitação."));

        sanitaryInspection.setStatus(InspectionStatus.PENDENTE);
        inspectionRepository.save(sanitaryInspection);

        updateRequestPhotosIfProvided(request, dto);

        request.setStatus(RequestStatus.EM_VISTORIA_FISCAL_E_SANITARIA);
        Request savedRequest = requestRepository.save(request);

        statusHistoryService.logStatusChange(
                savedRequest,
                RequestStatus.AGUARDANDO_CORRECAO,
                RequestStatus.EM_VISTORIA_FISCAL_E_SANITARIA,
                applicant
        );

        return requestMapper.toRequestDetailsDTO(savedRequest);
    }

    private void updateRequestPhotosIfProvided(Request request, RequestDTOs.SubmitCorrectionResponseDTO dto) {
        if (dto.newPhotoFrontUrl() != null && !dto.newPhotoFrontUrl().isBlank()) {
            request.setPhotoFrontUrl(dto.newPhotoFrontUrl());
        }
        if (dto.newPhotoLeftUrl() != null && !dto.newPhotoLeftUrl().isBlank()) {
            request.setPhotoLeftUrl(dto.newPhotoLeftUrl());
        }
        if (dto.newPhotoRightUrl() != null && !dto.newPhotoRightUrl().isBlank()) {
            request.setPhotoRightUrl(dto.newPhotoRightUrl());
        }
        if (dto.newPhotoBackUrl() != null && !dto.newPhotoBackUrl().isBlank()) {
            request.setPhotoBackUrl(dto.newPhotoBackUrl());
        }
        if (dto.newCnpjDocumentUrl() != null && !dto.newCnpjDocumentUrl().isBlank()) {
            if (request.getOwnerType() == br.gov.formosa.sigo2.model.enums.OwnerType.CNPJ) {
                request.setCnpjDocumentUrl(dto.newCnpjDocumentUrl());
            }
        }
    }
}