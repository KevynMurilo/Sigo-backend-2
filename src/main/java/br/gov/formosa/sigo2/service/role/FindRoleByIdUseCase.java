package br.gov.formosa.sigo2.service.role;

import br.gov.formosa.sigo2.dto.RoleDTOs;
import br.gov.formosa.sigo2.mapper.RoleMapper;
import br.gov.formosa.sigo2.model.Role;
import br.gov.formosa.sigo2.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FindRoleByIdUseCase {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Transactional(readOnly = true)
    public RoleDTOs.RoleResponseDTO execute(UUID id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Papel não encontrado: " + id));
        return roleMapper.toRoleResponseDTO(role);
    }
}