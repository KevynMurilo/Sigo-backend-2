package br.gov.formosa.sigo2.service.user;

import br.gov.formosa.sigo2.model.User;
import br.gov.formosa.sigo2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListAllUserUseCase {

    private final UserRepository userRepository;

    public Page<User> listAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
}
