package br.gov.formosa.sigo2.service.template;

import br.gov.formosa.sigo2.dto.TemplateDTOs;
import br.gov.formosa.sigo2.mapper.ChecklistTemplateMapper;
import br.gov.formosa.sigo2.model.ChecklistTemplate;
import br.gov.formosa.sigo2.model.ChecklistTemplateItem;
import br.gov.formosa.sigo2.repository.ChecklistTemplateRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateChecklistTemplateUseCase {

    private final ChecklistTemplateRepository templateRepository;
    private final ChecklistTemplateMapper templateMapper;

    @Transactional
    public TemplateDTOs.ChecklistTemplateDetailsDTO execute(TemplateDTOs.CreateChecklistTemplateDTO dto) {

        templateRepository.findByName(dto.name()).ifPresent(t -> {
            throw new ValidationException("Um template com este nome já existe.");
        });

        ChecklistTemplate template = templateMapper.fromCreateTemplateDTO(dto);

        List<ChecklistTemplateItem> items = templateMapper.fromCreateChecklistItemDTOList(dto.items());
        items.forEach(item -> item.setTemplate(template));
        template.setItems(items);

        ChecklistTemplate savedTemplate = templateRepository.save(template);
        return templateMapper.toTemplateDetailsDTO(savedTemplate);
    }
}