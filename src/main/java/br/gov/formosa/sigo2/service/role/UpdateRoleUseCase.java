package br.gov.formosa.sigo2.service.role;

import br.gov.formosa.sigo2.dto.RoleDTOs;
import br.gov.formosa.sigo2.mapper.RoleMapper;
import br.gov.formosa.sigo2.model.Role;
import br.gov.formosa.sigo2.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateRoleUseCase {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Transactional
    public RoleDTOs.RoleResponseDTO execute(UUID id, RoleDTOs.UpdateRoleDTO dto) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Papel não encontrado: " + id));

        roleRepository.findByNameIgnoreCase(dto.name()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new ValidationException("Papel com este nome já existe.");
            }
        });

        roleMapper.updateFromDTO(dto, role);
        Role updatedRole = roleRepository.save(role);
        return roleMapper.toRoleResponseDTO(updatedRole);
    }
}