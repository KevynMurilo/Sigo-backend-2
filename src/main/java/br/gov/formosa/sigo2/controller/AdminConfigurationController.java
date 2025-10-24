package br.gov.formosa.sigo2.controller.admin;

import br.gov.formosa.sigo2.dto.ConfigDTOs;
import br.gov.formosa.sigo2.service.config.CreateOrUpdateConfigUseCase;
import br.gov.formosa.sigo2.service.config.DeleteConfigUseCase;
import br.gov.formosa.sigo2.service.config.GetConfigByKeyUseCase;
import br.gov.formosa.sigo2.service.config.ListAllConfigsUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/configurations")
@RequiredArgsConstructor
public class AdminConfigurationController {

    private final CreateOrUpdateConfigUseCase createOrUpdateConfigUseCase;
    private final ListAllConfigsUseCase listAllConfigsUseCase;
    private final GetConfigByKeyUseCase getConfigByKeyUseCase;
    private final DeleteConfigUseCase deleteConfigUseCase;

    @PostMapping
    public ResponseEntity<ConfigDTOs.ConfigurationDTO> createOrUpdate(
            @Valid @RequestBody ConfigDTOs.ConfigurationDTO dto) {

        ConfigDTOs.ConfigurationDTO config = createOrUpdateConfigUseCase.execute(dto);
        return ResponseEntity.ok(config);
    }

    @GetMapping
    public ResponseEntity<Page<ConfigDTOs.ConfigurationDTO>> listAll(
            @PageableDefault Pageable pageable) {

        Page<ConfigDTOs.ConfigurationDTO> page = listAllConfigsUseCase.execute(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{key}")
    public ResponseEntity<ConfigDTOs.ConfigurationDTO> getByKey(@PathVariable String key) {
        ConfigDTOs.ConfigurationDTO config = getConfigByKeyUseCase.execute(key);
        return ResponseEntity.ok(config);
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Void> delete(@PathVariable String key) {
        deleteConfigUseCase.execute(key);
        return ResponseEntity.noContent().build();
    }
}