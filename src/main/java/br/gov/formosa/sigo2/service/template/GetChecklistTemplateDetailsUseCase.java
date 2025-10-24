package br.gov.formosa.sigo2.service.template;

import br.gov.formosa.sigo2.dto.TemplateDTOs;
import br.gov.formosa.sigo2.mapper.ChecklistTemplateMapper;
import br.gov.formosa.sigo2.model.ChecklistTemplate;
import br.gov.formosa.sigo2.repository.ChecklistTemplateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetChecklistTemplateDetailsUseCase {

    private final ChecklistTemplateRepository templateRepository;
    private final ChecklistTemplateMapper templateMapper;

    @Transactional(readOnly = true)
    public TemplateDTOs.ChecklistTemplateDetailsDTO execute(UUID templateId) {
        ChecklistTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new EntityNotFoundException("Template não encontrado: " + templateId));

        return templateMapper.toTemplateDetailsDTO(template);
    }
}