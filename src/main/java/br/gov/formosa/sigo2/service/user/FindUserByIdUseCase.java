package br.gov.formosa.sigo2.service.user;

import br.gov.formosa.sigo2.dto.UserDTOs;
import br.gov.formosa.sigo2.mapper.UserMapper;
import br.gov.formosa.sigo2.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FindUserByIdUseCase {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserDTOs.UserResponseDTO execute(UUID userId) {
        return userRepository.findById(userId)
                .map(userMapper::toUserResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + userId));
    }
}