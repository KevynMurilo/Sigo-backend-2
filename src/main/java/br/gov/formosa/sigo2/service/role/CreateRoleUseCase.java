package br.gov.formosa.sigo2.service.role;

import br.gov.formosa.sigo2.model.Role;
import br.gov.formosa.sigo2.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateRoleUseCase {

    private final RoleRepository roleRepository;

    public Role execute(Role role) {
        if (checkRoleName(role.getName())) {
            throw new IllegalArgumentException("Role name already exists");
        }

        return roleRepository.save(role);
    }

    private boolean checkRoleName(String name) {
        return roleRepository.findByNameIgnoreCase(name).isPresent();
    }
}
