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
import br.gov.formosa.sigo2.repository.UserRepository;
import br.gov.formosa.sigo2.service.util.ConfigurationService;
import br.gov.formosa.sigo2.service.util.StatusHistoryService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TriageRequestUseCase {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final InspectionRepository inspectionRepository;
    private final StatusHistoryService statusHistoryService;
    private final ConfigurationService configurationService;
    private final RequestMapper requestMapper;

    @Transactional
    public RequestDTOs.RequestDetailsDTO execute(UUID requestId, RequestDTOs.TriageRequestDTO dto, User secretario) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação não encontrada: " + requestId));

        if (request.getStatus() != RequestStatus.NOVA) {
            throw new ValidationException("Solicitação não está no status 'NOVA' e não pode ser triada.");
        }

        User fiscal = userRepository.findById(dto.fiscalUserId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário Fiscal não encontrado."));

        List<Inspection> newInspections = new ArrayList<>();
        newInspections.add(createInspection(request, fiscal, InspectionType.FISCAL));

        RequestStatus newStatus;

        boolean requiresSanitary = configurationService.isSanitaryRequired(request.getCommerceType());

        if (requiresSanitary) {
            if (dto.sanitaryUserId() == null) {
                throw new ValidationException("Vistoria sanitária é obrigatória para este tipo de comércio.");
            }
            User vigilante = userRepository.findById(dto.sanitaryUserId())
                    .orElseThrow(() -> new EntityNotFoundException("Usuário Vigilante Sanitário não encontrado."));

            newInspections.add(createInspection(request, vigilante, InspectionType.SANITARIA));
            newStatus = RequestStatus.EM_VISTORIA_FISCAL_E_SANITARIA;
        } else {
            newStatus = RequestStatus.EM_VISTORIA;
        }

        inspectionRepository.saveAll(newInspections);

        request.setStatus(newStatus);
        request.getInspections().addAll(newInspections);
        Request savedRequest = requestRepository.save(request);

        statusHistoryService.logStatusChange(savedRequest, RequestStatus.NOVA, newStatus, secretario);

        return requestMapper.toRequestDetailsDTO(savedRequest);
    }

    private Inspection createInspection(Request request, User inspector, InspectionType type) {
        Inspection inspection = new Inspection();
        inspection.setRequest(request);
        inspection.setInspector(inspector);
        inspection.setType(type);
        inspection.setStatus(InspectionStatus.PENDENTE);
        return inspection;
    }
}