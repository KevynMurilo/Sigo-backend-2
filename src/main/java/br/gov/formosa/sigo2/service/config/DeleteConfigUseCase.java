package br.gov.formosa.sigo2.service.config;

import br.gov.formosa.sigo2.repository.ConfigurationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteConfigUseCase {

    private final ConfigurationRepository configurationRepository;

    @Transactional
    public void execute(String key) {
        if (!configurationRepository.existsById(key)) {
            throw new EntityNotFoundException("Configuração não encontrada: " + key);
        }
        configurationRepository.deleteById(key);
    }
}