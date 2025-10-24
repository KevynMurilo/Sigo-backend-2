package br.gov.formosa.sigo2.controller;

import br.gov.formosa.sigo2.dto.AuthDTOs;
import br.gov.formosa.sigo2.service.auth.ConectaIdCallbackUseCase;
import br.gov.formosa.sigo2.service.auth.LoginUseCase;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController {

    private final LoginUseCase loginUseCase;
    private final ConectaIdCallbackUseCase conectaIdCallbackUseCase;

    private record LoginRequest(String email, String cpf) {}

    @GetMapping("/conectaid/callback")
    public ResponseEntity<AuthDTOs.LoginResponseDTO> handleCallback(
            @RequestParam("code") String code,
            HttpServletResponse response) {

        AuthDTOs.LoginResponseDTO loginResponse = conectaIdCallbackUseCase.execute(code, response);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(loginUseCase.login(loginRequest.email, loginRequest.cpf));
    }
}
