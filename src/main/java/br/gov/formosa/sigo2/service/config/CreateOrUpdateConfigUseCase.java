package br.gov.formosa.sigo2.service.config;

import br.gov.formosa.sigo2.dto.ConfigDTOs;
import br.gov.formosa.sigo2.mapper.ConfigurationMapper;
import br.gov.formosa.sigo2.model.Configuration;
import br.gov.formosa.sigo2.repository.ConfigurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreateOrUpdateConfigUseCase {

    private final ConfigurationRepository configurationRepository;
    private final ConfigurationMapper configurationMapper;

    @Transactional
    public ConfigDTOs.ConfigurationDTO execute(ConfigDTOs.ConfigurationDTO dto) {

        Optional<Configuration> existingConfigOpt = configurationRepository.findByKey(dto.key());

        Configuration configToSave;

        if (existingConfigOpt.isPresent()) {
            configToSave = existingConfigOpt.get();
            configurationMapper.updateFromDTO(dto, configToSave);
        } else {
            configToSave = configurationMapper.fromConfigurationDTO(dto);
        }

        Configuration savedConfig = configurationRepository.save(configToSave);
        return configurationMapper.toConfigurationDTO(savedConfig);
    }
}