package br.gov.formosa.sigo2.service.inspection;

import br.gov.formosa.sigo2.dto.InspectionDTOs;
import br.gov.formosa.sigo2.mapper.InspectionMapper;
import br.gov.formosa.sigo2.model.*;
import br.gov.formosa.sigo2.model.enums.InspectionStatus;
import br.gov.formosa.sigo2.model.enums.RequestStatus;
import br.gov.formosa.sigo2.repository.ChecklistTemplateItemRepository;
import br.gov.formosa.sigo2.repository.InspectionRepository;
import br.gov.formosa.sigo2.repository.RequestRepository;
import br.gov.formosa.sigo2.service.util.StatusHistoryService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubmitInspectionApprovalUseCase {

    private final InspectionRepository inspectionRepository;
    private final ChecklistTemplateItemRepository templateItemRepository;
    private final RequestRepository requestRepository;
    private final InspectionMapper inspectionMapper;
    private final StatusHistoryService statusHistoryService;

    @Transactional
    public InspectionDTOs.FullInspectionResponseDTO execute(UUID inspectionId, InspectionDTOs.SubmitInspectionDTO dto, User inspector) {

        Inspection inspection = findAndValidateInspection(inspectionId, inspector);

        List<InspectionChecklistItem> items = mapAnswersToItems(inspection, dto.answers());
        List<InspectionEvidence> evidence = mapEvidenceToItems(inspection, dto.evidence());

        inspection.getInspectionItems().clear();
        inspection.getEvidence().clear();
        inspection.getInspectionItems().addAll(items);
        inspection.getEvidence().addAll(evidence);

        inspection.setStatus(InspectionStatus.APROVADA);
        inspection.setInspectionDate(LocalDateTime.now());
        inspection.setCalculatedFee(dto.calculatedFee());
        inspection.setObservations(dto.observations());

        Inspection savedInspection = inspectionRepository.save(inspection);

        // Passa o ID da request para o método de verificação
        checkRequestStatus(savedInspection.getRequest().getId(), inspector);

        return inspectionMapper.toFullInspectionResponseDTO(savedInspection);
    }

    private Inspection findAndValidateInspection(UUID inspectionId, User inspector) {
        Inspection inspection = inspectionRepository.findById(inspectionId)
                .orElseThrow(() -> new EntityNotFoundException("Vistoria não encontrada."));

        if (!inspection.getInspector().equals(inspector)) {
            throw new AccessDeniedException("Usuário não é o responsável por esta vistoria.");
        }
        if (inspection.getStatus() != InspectionStatus.PENDENTE) {
            throw new ValidationException("Vistoria não está mais pendente.");
        }
        return inspection;
    }

    private List<InspectionChecklistItem> mapAnswersToItems(Inspection inspection, List<InspectionDTOs.SubmittedAnswerDTO> answers) {
        Set<UUID> templateItemIds = answers.stream()
                .map(InspectionDTOs.SubmittedAnswerDTO::templateItemId)
                .collect(Collectors.toSet());

        Map<UUID, ChecklistTemplateItem> templateItemMap = templateItemRepository.findMapByIds(templateItemIds);

        return answers.stream().map(answerDto -> {
            ChecklistTemplateItem templateItem = templateItemMap.get(answerDto.templateItemId());
            if (templateItem == null) {
                throw new EntityNotFoundException("Item de checklist não encontrado: " + answerDto.templateItemId());
            }

            InspectionChecklistItem item = inspectionMapper.submittedAnswerToItem(answerDto);
            item.setInspection(inspection);
            item.setQuestionText(templateItem.getQuestionText());
            item.setQuestionType(templateItem.getQuestionType());
            item.setItemOrder(templateItem.getItemOrder());
            item.setOptionsJson(templateItem.getOptionsJson());
            return item;
        }).collect(Collectors.toList());
    }

    private List<InspectionEvidence> mapEvidenceToItems(Inspection inspection, List<InspectionDTOs.SubmittedEvidenceDTO> evidenceDtos) {
        if (evidenceDtos == null) {
            return List.of();
        }
        return evidenceDtos.stream().map(dto -> {
            InspectionEvidence evidence = inspectionMapper.submittedEvidenceToEvidence(dto);
            evidence.setInspection(inspection);
            return evidence;
        }).collect(Collectors.toList());
    }

    private void checkRequestStatus(UUID requestId, User inspector) {
        Request freshRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação não encontrada após salvar vistoria: " + requestId));

        boolean allApproved = freshRequest.getInspections().stream()
                .allMatch(insp -> insp.getStatus() == InspectionStatus.APROVADA);

        if (allApproved) {
            RequestStatus oldStatus = freshRequest.getStatus();
            freshRequest.setStatus(RequestStatus.AGUARDANDO_EMISSAO_BOLETO);
            requestRepository.save(freshRequest);
            statusHistoryService.logStatusChange(freshRequest, oldStatus, RequestStatus.AGUARDANDO_EMISSAO_BOLETO, inspector);
        }
    }
}