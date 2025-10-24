package br.gov.formosa.sigo2.service.role;

import br.gov.formosa.sigo2.repository.RoleRepository;
import br.gov.formosa.sigo2.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteRoleUseCase {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Transactional
    public void execute(UUID id) {
        if (!roleRepository.existsById(id)) {
            throw new EntityNotFoundException("Papel não encontrado: " + id);
        }

        if (userRepository.existsByRoleId(id)) {
            throw new ValidationException("Não é possível deletar o papel pois ele está sendo usado por usuários.");
        }

        roleRepository.deleteById(id);
    }
}