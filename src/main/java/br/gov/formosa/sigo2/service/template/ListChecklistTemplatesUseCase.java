package br.gov.formosa.sigo2.service.template;

import br.gov.formosa.sigo2.dto.TemplateDTOs;
import br.gov.formosa.sigo2.mapper.ChecklistTemplateMapper;
import br.gov.formosa.sigo2.model.ChecklistTemplate;
import br.gov.formosa.sigo2.repository.ChecklistTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListChecklistTemplatesUseCase {

    private final ChecklistTemplateRepository templateRepository;
    private final ChecklistTemplateMapper templateMapper;

    @Transactional(readOnly = true)
    public Page<TemplateDTOs.ChecklistTemplateSummaryDTO> execute(Pageable pageable) {
        Page<ChecklistTemplate> page = templateRepository.findAll(pageable);
        return templateMapper.toTemplateSummaryDTOPage(page);
    }
}