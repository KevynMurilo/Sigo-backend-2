package br.gov.formosa.sigo2.controller;

import br.gov.formosa.sigo2.dto.RoleDTOs;
import br.gov.formosa.sigo2.service.role.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final CreateRoleUseCase createRoleUseCase;
    private final ListAllRoleUseCase listAllRoleUseCase;
    private final FindRoleByIdUseCase findRoleByIdUseCase;
    private final UpdateRoleUseCase updateRoleUseCase;
    private final DeleteRoleUseCase deleteRoleUseCase;

    @PostMapping
    public ResponseEntity<RoleDTOs.RoleResponseDTO> create(@Valid @RequestBody RoleDTOs.CreateRoleDTO dto) {
        RoleDTOs.RoleResponseDTO createdRole = createRoleUseCase.execute(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdRole.id())
                .toUri();
        return ResponseEntity.created(location).body(createdRole);
    }

    @GetMapping
    public ResponseEntity<List<RoleDTOs.RoleResponseDTO>> list() {
        return ResponseEntity.ok(listAllRoleUseCase.execute());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleDTOs.RoleResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(findRoleByIdUseCase.execute(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleDTOs.RoleResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody RoleDTOs.UpdateRoleDTO dto) {
        return ResponseEntity.ok(updateRoleUseCase.execute(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteRoleUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}