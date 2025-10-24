package br.gov.formosa.sigo2.service.user;

import br.gov.formosa.sigo2.dto.UserDTOs;
import br.gov.formosa.sigo2.mapper.UserMapper;
import br.gov.formosa.sigo2.model.Role;
import br.gov.formosa.sigo2.model.User;
import br.gov.formosa.sigo2.repository.RoleRepository;
import br.gov.formosa.sigo2.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminCreateInternalUserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserDTOs.UserResponseDTO execute(UserDTOs.AdminCreateUserDTO dto) {
        if (userRepository.findByCpf(dto.cpf()).isPresent()) {
            throw new IllegalArgumentException("CPF já cadastrado.");
        }
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado.");
        }

        Role role = roleRepository.findById(dto.roleId())
                .orElseThrow(() -> new EntityNotFoundException("Papel não encontrado: " + dto.roleId()));

        User newUser = userMapper.adminCreateDTOToUser(dto);
        newUser.setRole(role);
        newUser.setOnboardingCompleted(true);

        User savedUser = userRepository.save(newUser);
        return userMapper.toUserResponseDTO(savedUser);
    }
}