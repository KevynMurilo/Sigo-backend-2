package br.gov.formosa.sigo2.service.util;

import br.gov.formosa.sigo2.model.Configuration;
import br.gov.formosa.sigo2.repository.ConfigurationRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ConfigurationService {

    private final ConfigurationRepository configurationRepository;
    private final ObjectMapper objectMapper;

    private String getValue(String key) {
        return configurationRepository.findByKey(key)
                .map(Configuration::getValue)
                .orElseThrow(() -> new EntityNotFoundException("Configuração não encontrada: " + key));
    }

    public BigDecimal getDecimal(String key) {
        try {
            return new BigDecimal(getValue(key));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Configuração " + key + " não é um número decimal válido.");
        }
    }

    public Integer getInteger(String key) {
        try {
            return Integer.parseInt(getValue(key));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Configuração " + key + " não é um número inteiro válido.");
        }
    }

    public Set<String> getStringSet(String key) {
        try {
            String json = getValue(key);
            return objectMapper.readValue(json, new TypeReference<Set<String>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Configuração " + key + " não é um JSON Set<String> válido.");
        }
    }

    public boolean isSanitaryRequired(String commerceType) {
        return getStringSet("SANITARY_COMMERCE_TYPES")
                .stream().anyMatch(type -> type.equalsIgnoreCase(commerceType));
    }

    public Set<String> getSanitaryInspectionCommerceTypes() {
        return Set.of("Alimentação - Lanches", "Alimentação");
    }
}