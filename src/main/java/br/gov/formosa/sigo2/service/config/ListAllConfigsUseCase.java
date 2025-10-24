package br.gov.formosa.sigo2.service.config;

import br.gov.formosa.sigo2.dto.ConfigDTOs;
import br.gov.formosa.sigo2.mapper.ConfigurationMapper;
import br.gov.formosa.sigo2.model.Configuration;
import br.gov.formosa.sigo2.repository.ConfigurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListAllConfigsUseCase {

    private final ConfigurationRepository configurationRepository;
    private final ConfigurationMapper configurationMapper;

    @Transactional(readOnly = true)
    public Page<ConfigDTOs.ConfigurationDTO> execute(Pageable pageable) {
        Page<Configuration> page = configurationRepository.findAll(pageable);
        return configurationMapper.toConfigurationDTOPage(page);
    }
}