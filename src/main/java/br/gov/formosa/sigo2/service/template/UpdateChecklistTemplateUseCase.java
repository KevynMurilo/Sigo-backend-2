package br.gov.formosa.sigo2.service.template;

import br.gov.formosa.sigo2.dto.TemplateDTOs;
import br.gov.formosa.sigo2.mapper.ChecklistTemplateMapper;
import br.gov.formosa.sigo2.model.ChecklistTemplate;
import br.gov.formosa.sigo2.model.ChecklistTemplateItem;
import br.gov.formosa.sigo2.repository.ChecklistTemplateItemRepository;
import br.gov.formosa.sigo2.repository.ChecklistTemplateRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UpdateChecklistTemplateUseCase {

    private final ChecklistTemplateRepository templateRepository;
    private final ChecklistTemplateItemRepository itemRepository;
    private final ChecklistTemplateMapper templateMapper;

    @Transactional
    public TemplateDTOs.ChecklistTemplateDetailsDTO execute(UUID templateId, TemplateDTOs.UpdateChecklistTemplateDTO dto) {

        ChecklistTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new EntityNotFoundException("Template não encontrado: " + templateId));

        templateRepository.findByName(dto.name()).ifPresent(existing -> {
            if (!existing.getId().equals(templateId)) {
                throw new ValidationException("Um template com este nome já existe.");
            }
        });

        templateMapper.updateTemplateFromDTO(dto, template);

        List<ChecklistTemplateItem> newItems = templateMapper.fromCreateChecklistItemDTOList(dto.items());
        newItems.forEach(item -> item.setTemplate(template));

        template.getItems().clear();
        template.getItems().addAll(newItems);

        ChecklistTemplate savedTemplate = templateRepository.save(template);
        return templateMapper.toTemplateDetailsDTO(savedTemplate);
    }
}