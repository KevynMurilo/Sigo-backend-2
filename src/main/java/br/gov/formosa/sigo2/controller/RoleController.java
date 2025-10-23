package br.gov.formosa.sigo2.controller;

import br.gov.formosa.sigo2.model.Role;
import br.gov.formosa.sigo2.service.role.CreateRoleUseCase;
import br.gov.formosa.sigo2.service.role.ListAllRoleUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final CreateRoleUseCase createRoleUseCase;
    private final ListAllRoleUseCase listAllRoleUseCase;

    @PostMapping
    public ResponseEntity<Role> execute(@Valid @RequestBody Role role) {
        return ResponseEntity.ok().body(createRoleUseCase.execute(role));
    }

    @GetMapping
    public ResponseEntity<List<Role>> list() {
        return ResponseEntity.ok().body(listAllRoleUseCase.execute());
    }
}
