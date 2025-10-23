package br.gov.formosa.sigo2.config;

import br.gov.formosa.sigo2.model.Role;
import br.gov.formosa.sigo2.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeederConfig implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        seedCheckRoles();
    }

    private void seedCheckRoles() {
        if (roleRepository.findAll().isEmpty()) {
            List<Role> defaultRoles = Arrays.asList(
                    createRole("SOLICITANTE"),
                    createRole("CIDADAO_DENUNCIANTE"),
                    createRole("SECRETARIO"),
                    createRole("FISCAL"),
                    createRole("VIGILANTE_SANITARIO"),
                    createRole("ADMINISTRATIVO"),
                    createRole("ADMIN_MASTER")
            );

            roleRepository.saveAll(defaultRoles);
            System.out.println("Roles padrão criadas com sucesso!");
        } else {
            System.out.println("Roles já existentes, seed ignorado");
        }
    }

    private Role createRole(String name) {
        Role role = new Role();
        role.setName(name);
        return role;
    }
}
