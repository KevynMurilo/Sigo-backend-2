package br.gov.formosa.sigo2.service.auth;

import br.gov.formosa.sigo2.model.User;
import br.gov.formosa.sigo2.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginUseCase {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public String login(String email, String cpf) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        cpf
                )
        );
        User user = (User) authentication.getPrincipal();
        return jwtTokenProvider.generateToken(user);
    }
}
