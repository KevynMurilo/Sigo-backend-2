package br.gov.formosa.sigo2.service.user;

import br.gov.formosa.sigo2.model.User;
import br.gov.formosa.sigo2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterUserUseCase {

    private final UserRepository userRepository;

    public User execute(User user) {
        if (checkEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (checkCPF(user.getCpf())) {
            throw new IllegalArgumentException("Cpf already exists");
        }

        return userRepository.save(user);
    }

    public boolean checkEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean checkCPF(String cpf) {
        return userRepository.findByCpf(cpf).isPresent();
    }
}
