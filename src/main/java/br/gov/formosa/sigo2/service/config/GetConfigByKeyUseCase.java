package br.gov.formosa.sigo2.service.config;

import br.gov.formosa.sigo2.dto.ConfigDTOs;
import br.gov.formosa.sigo2.mapper.ConfigurationMapper;
import br.gov.formosa.sigo2.model.Configuration;
import br.gov.formosa.sigo2.repository.ConfigurationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetConfigByKeyUseCase {

    private final ConfigurationRepository configurationRepository;
    private final ConfigurationMapper configurationMapper;

    @Transactional(readOnly = true)
    public ConfigDTOs.ConfigurationDTO execute(String key) {
        Configuration config = configurationRepository.findByKey(key)
                .orElseThrow(() -> new EntityNotFoundException("Configuração não encontrada: " + key));

        return configurationMapper.toConfigurationDTO(config);
    }
}