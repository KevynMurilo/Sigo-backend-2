package br.gov.formosa.sigo2.controller;

import br.gov.formosa.sigo2.model.User;
import br.gov.formosa.sigo2.service.user.ListAllUserUseCase;
import br.gov.formosa.sigo2.service.user.RegisterUserUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final RegisterUserUseCase registerUserUseCase;
    private final ListAllUserUseCase listAllUserUseCase;

    @GetMapping("/users")
    public Page<User> listarUsuarios(@RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        return listAllUserUseCase.listAllUsers(pageable);
    }

    @PostMapping
    public ResponseEntity<User> register(@Valid @RequestBody User user) {
        return ResponseEntity.ok(registerUserUseCase.execute(user));
    }
}
