package br.gov.formosa.sigo2.service.util;

import org.springframework.stereotype.Service;
import java.util.Set;

@Service
public class ConfigurationService {
    public Set<String> getSanitaryInspectionCommerceTypes() {
        return Set.of("Alimentação - Lanches", "Alimentação");
    }
}