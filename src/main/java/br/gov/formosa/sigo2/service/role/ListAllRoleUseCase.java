package br.gov.formosa.sigo2.service.role;

import br.gov.formosa.sigo2.dto.RoleDTOs;
import br.gov.formosa.sigo2.mapper.RoleMapper;
import br.gov.formosa.sigo2.model.Role;
import br.gov.formosa.sigo2.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListAllRoleUseCase {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Transactional(readOnly = true)
    public List<RoleDTOs.RoleResponseDTO> execute() {
        List<Role> roles = roleRepository.findAll();

        return roleMapper.toRoleResponseDTOList(roles);
    }
}