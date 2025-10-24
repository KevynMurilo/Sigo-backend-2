package br.gov.formosa.sigo2.service.user;

import br.gov.formosa.sigo2.model.Role;
import br.gov.formosa.sigo2.model.User;
import br.gov.formosa.sigo2.repository.RoleRepository;
import br.gov.formosa.sigo2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class FindOrCreateUserBySsoUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public record SsoUserData(String cpf, String fullName, String email) {}

    @Transactional
    public User execute(SsoUserData ssoData) {
        log.info("Processando login SSO para o CPF: {}", ssoData.cpf());

        Optional<User> existingUser = userRepository.findByCpf(ssoData.cpf());

        if (existingUser.isPresent()) {
            log.info("Usuário encontrado (ID: {}). Retornando.", existingUser.get().getId());
            return existingUser.get();
        }

        log.info("Primeiro acesso para o CPF. Criando novo usuário.");

        if (userRepository.findByEmail(ssoData.email()).isPresent()) {
            log.error("Conflito: CPF é novo, mas o email {} já existe.", ssoData.email());
            throw new IllegalArgumentException("Email já cadastrado para outro CPF.");
        }

        Role defaultRole = roleRepository.findByNameIgnoreCase("SOLICITANTE")
                .orElseThrow(() -> new IllegalStateException("Papel 'SOLICITANTE' não encontrado."));

        User newUser = new User();
        newUser.setCpf(ssoData.cpf());
        newUser.setFullName(ssoData.fullName());
        newUser.setEmail(ssoData.email());
        newUser.setRole(defaultRole);
        newUser.setOnboardingCompleted(false);

        return userRepository.save(newUser);
    }
}