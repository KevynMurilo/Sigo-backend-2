package br.gov.formosa.sigo2.controller;

import br.gov.formosa.sigo2.dto.AuthDTOs;
import br.gov.formosa.sigo2.mapper.AuthMapper;
import br.gov.formosa.sigo2.model.User;
import br.gov.formosa.sigo2.security.JwtTokenProvider;
import br.gov.formosa.sigo2.service.auth.ConectaIdCallbackUseCase;
import br.gov.formosa.sigo2.service.auth.LoginUseCase;
import br.gov.formosa.sigo2.service.user.FindOrCreateUserBySsoUseCase;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController {

    private final LoginUseCase loginUseCase;
    private final ConectaIdCallbackUseCase conectaIdCallbackUseCase;
    private final FindOrCreateUserBySsoUseCase findOrCreateUserBySsoUseCase;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthMapper authMapper;

    @Value( "${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    private record LoginRequest(String email, String cpf) {}

    @Profile("dev")
    @PostMapping("/conectaid/simulate")
    public ResponseEntity<AuthDTOs.LoginResponseDTO> simulateLogin(
            @Valid @RequestBody AuthDTOs.SimulateLoginRequestDTO dto,
            HttpServletResponse response) {

        FindOrCreateUserBySsoUseCase.SsoUserData ssoData = new FindOrCreateUserBySsoUseCase.SsoUserData(
                dto.cpf(), dto.fullName(), dto.email()
        );

        User user = findOrCreateUserBySsoUseCase.execute(ssoData);

        String jwtToken = jwtTokenProvider.generateToken(user);

        addJwtCookieToResponse(response, jwtToken);

        AuthDTOs.LoginResponseDTO responseDto = authMapper.toLoginResponseDTO(user);
        responseDto = new AuthDTOs.LoginResponseDTO(
                responseDto.userId(), responseDto.fullName(), responseDto.email(),
                responseDto.roleName(), responseDto.onboardingCompleted(), jwtToken
        );

        return ResponseEntity.ok(responseDto);
    }

    private void addJwtCookieToResponse(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from("sigo_token", token)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(Duration.ofMillis(jwtExpirationMs))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

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
