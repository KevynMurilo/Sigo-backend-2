package br.gov.formosa.sigo2.service.inspection;

import br.gov.formosa.sigo2.dto.InspectionDTOs;
import br.gov.formosa.sigo2.dto.RequestDTOs;
import br.gov.formosa.sigo2.mapper.InspectionMapper;
import br.gov.formosa.sigo2.model.ChecklistTemplate;
import br.gov.formosa.sigo2.model.Inspection;
import br.gov.formosa.sigo2.model.User;
import br.gov.formosa.sigo2.repository.ChecklistTemplateRepository;
import br.gov.formosa.sigo2.repository.InspectionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetInspectionDetailsUseCase {

    private final InspectionRepository inspectionRepository;
    private final ChecklistTemplateRepository templateRepository;
    private final InspectionMapper inspectionMapper;

    @Transactional(readOnly = true)
    public InspectionDTOs.InspectionDetailsDTO execute(UUID inspectionId, User inspector) {

        Inspection inspection = inspectionRepository.findById(inspectionId)
                .orElseThrow(() -> new EntityNotFoundException("Vistoria não encontrada."));

        if (!inspection.getInspector().equals(inspector)) {
            throw new AccessDeniedException("Usuário não é o responsável por esta vistoria.");
        }

        ChecklistTemplate template = templateRepository.findByTypeAndActiveTrue(inspection.getType())
                .orElseThrow(() -> new EntityNotFoundException("Template de checklist não encontrado para o tipo: " + inspection.getType()));

        List<InspectionDTOs.ChecklistQuestionDTO> questions = template.getItems().stream()
                .map(inspectionMapper::toChecklistQuestionDTO)
                .collect(Collectors.toList());

        RequestDTOs.LocationDTO locationDTO = new RequestDTOs.LocationDTO(
                inspection.getRequest().getLocation().getLatitude(),
                inspection.getRequest().getLocation().getLongitude()
        );

        return new InspectionDTOs.InspectionDetailsDTO(
                inspection.getId(),
                inspection.getType(),
                inspection.getStatus(),
                inspection.getRequest().getId(),
                inspection.getRequest().getProtocol(),
                inspection.getRequest().getTradeName(),
                locationDTO,
                questions
        );
    }
}