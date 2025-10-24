package br.gov.formosa.sigo2.service.user;

import br.gov.formosa.sigo2.dto.UserDTOs;
import br.gov.formosa.sigo2.mapper.UserMapper;
import br.gov.formosa.sigo2.model.User;
import br.gov.formosa.sigo2.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompleteUserOnboardingUseCase {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserDTOs.UserResponseDTO execute(UUID userId, UserDTOs.OnboardingDataDTO data) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + userId));

        user.setIdentityDocumentFrontUrl(data.identityDocumentFrontUrl());
        user.setIdentityDocumentBackUrl(data.identityDocumentBackUrl());
        user.setProofOfResidenceUrl(data.proofOfResidenceUrl());
        user.setOnboardingCompleted(true);

        User updatedUser = userRepository.save(user);
        return userMapper.toUserResponseDTO(updatedUser);
    }
}