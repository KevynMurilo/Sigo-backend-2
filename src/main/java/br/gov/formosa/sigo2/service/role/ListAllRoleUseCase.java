package br.gov.formosa.sigo2.service.role;

import br.gov.formosa.sigo2.model.Role;
import br.gov.formosa.sigo2.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListAllRoleUseCase {

    private final RoleRepository roleRepository;;

    public List<Role> execute() {
        return roleRepository.findAll();
    }
}
