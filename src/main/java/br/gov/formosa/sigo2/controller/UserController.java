package br.gov.formosa.sigo2.controller;

import br.gov.formosa.sigo2.dto.UserDTOs;
import br.gov.formosa.sigo2.model.User;
import br.gov.formosa.sigo2.service.user.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final AdminCreateInternalUserUseCase adminCreateInternalUserUseCase;
    private final ListAllUserUseCase listAllUserUseCase;
    private final FindUserByIdUseCase findUserByIdUseCase;
    private final AdminUpdateUserRoleUseCase adminUpdateUserRoleUseCase;
    private final CompleteUserOnboardingUseCase completeUserOnboardingUseCase;
    private final RegisterUserUseCase registerUserUseCase;

    @GetMapping("/me")
    public ResponseEntity<UserDTOs.UserResponseDTO> getMe(@AuthenticationPrincipal User currentUser) {
        UserDTOs.UserResponseDTO response = findUserByIdUseCase.execute(currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/me/onboarding")
    public ResponseEntity<UserDTOs.UserResponseDTO> completeOnboarding(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody UserDTOs.OnboardingDataDTO onboardingData) {

        UserDTOs.UserResponseDTO updatedUser = completeUserOnboardingUseCase.execute(currentUser.getId(), onboardingData);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping
    public ResponseEntity<Page<UserDTOs.UserResponseDTO>> listAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fullName,asc") String sort
    ) {
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        Page<UserDTOs.UserResponseDTO> result = listAllUserUseCase.execute(pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTOs.UserResponseDTO> findById(@PathVariable UUID id) {
        UserDTOs.UserResponseDTO user = findUserByIdUseCase.execute(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<UserDTOs.UserResponseDTO> adminCreateUser(
            @Valid @RequestBody UserDTOs.AdminCreateUserDTO userDto) {

        UserDTOs.UserResponseDTO createdUser = adminCreateInternalUserUseCase.execute(userDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUser.id())
                .toUri();

        return ResponseEntity.created(location).body(createdUser);
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<UserDTOs.UserResponseDTO> adminUpdateRole(
            @PathVariable UUID id,
            @Valid @RequestBody UserDTOs.AdminRoleUpdateDTO roleDto) {

        UserDTOs.UserResponseDTO updatedUser = adminUpdateUserRoleUseCase.execute(id, roleDto.newRoleId());
        return ResponseEntity.ok(updatedUser);
    }
}