package br.gov.formosa.sigo2.service.template;

import br.gov.formosa.sigo2.repository.ChecklistTemplateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteChecklistTemplateUseCase {

    private final ChecklistTemplateRepository templateRepository;

    @Transactional
    public void execute(UUID templateId) {
        if (!templateRepository.existsById(templateId)) {
            throw new EntityNotFoundException("Template não encontrado: " + templateId);
        }

        templateRepository.deleteById(templateId);
    }
}