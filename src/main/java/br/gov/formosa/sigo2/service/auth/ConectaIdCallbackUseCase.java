package br.gov.formosa.sigo2.service.auth;

import br.gov.formosa.sigo2.dto.AuthDTOs;
import br.gov.formosa.sigo2.mapper.AuthMapper;
import br.gov.formosa.sigo2.model.User;
import br.gov.formosa.sigo2.security.JwtTokenProvider;
import br.gov.formosa.sigo2.service.user.FindOrCreateUserBySsoUseCase;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConectaIdCallbackUseCase {

    private final FindOrCreateUserBySsoUseCase findOrCreateUserBySsoUseCase;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthMapper authMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${sso.provider.endpoints.token-uri}")
    private String tokenUrl;

    @Value("${sso.provider.endpoints.user-info-uri}")
    private String userInfoUrl;

    @Value("${sso.client.id}")
    private String clientId;

    @Value("${sso.client.secret}")
    private String clientSecret;

    @Value("${app.frontend.url}")
    private String redirectUri;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    public AuthDTOs.LoginResponseDTO execute(String code, HttpServletResponse response) {
        try {
            log.info("Recebido callback do ConectaID com code.");
            JsonNode tokenData = exchangeCodeForToken(code);
            String accessToken = tokenData.path("access_token").asText(null);

            if (accessToken == null) {
                log.error("Access token não retornado pelo ConectaID.");
                throw new RuntimeException("Access token não retornado pelo provedor ConectaID");
            }
            log.debug("Access token obtido com sucesso.");

            AuthDTOs.UserInfoDTO userInfo = fetchUserInfo(accessToken);
            log.info("Informações do usuário obtidas: CPF {}", userInfo.cpf() != null ? "***" : "N/A");

            FindOrCreateUserBySsoUseCase.SsoUserData ssoData = new FindOrCreateUserBySsoUseCase.SsoUserData(
                    userInfo.cpf(), userInfo.fullName(), userInfo.email()
            );

            User user = findOrCreateUserBySsoUseCase.execute(ssoData);
            log.info("Usuário {} encontrado/criado com ID: {}", user.getEmail(), user.getId());

            String jwtToken = jwtTokenProvider.generateToken(user); // Usa seu provider
            addJwtCookieToResponse(response, jwtToken);
            log.info("Token JWT gerado e adicionado ao cookie para usuário {}", user.getEmail());

            AuthDTOs.LoginResponseDTO responseDto = authMapper.toLoginResponseDTO(user);
            responseDto = new AuthDTOs.LoginResponseDTO(
                    responseDto.userId(), responseDto.fullName(), responseDto.email(),
                    responseDto.roleName(), responseDto.onboardingCompleted(), jwtToken
            );

            return responseDto;

        } catch (HttpClientErrorException e) {
            log.error("Erro HTTP na comunicação com ConectaID: {} - {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("Erro na comunicação com ConectaID: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("Falha inesperada na autenticação ConectaID: {}", e.getMessage(), e);
            throw new RuntimeException("Falha na autenticação ConectaID: " + e.getMessage(), e);
        }
    }

    private JsonNode exchangeCodeForToken(String code) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("code", code);
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("redirect_uri", redirectUri);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);

        log.debug("Enviando requisição de token para {}", tokenUrl);
        ResponseEntity<JsonNode> response = restTemplate.postForEntity(tokenUrl, request, JsonNode.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            log.error("Erro ao obter token do ConectaID. Status: {}, Body: {}", response.getStatusCode(), response.getBody());
            throw new HttpClientErrorException(response.getStatusCode(), "Erro ao obter token do ConectaID");
        }
        return response.getBody();
    }

    private AuthDTOs.UserInfoDTO fetchUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        log.debug("Buscando informações do usuário em {}", userInfoUrl);
        ResponseEntity<JsonNode> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, entity, JsonNode.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            log.error("Erro ao obter informações do usuário no ConectaID. Status: {}, Body: {}", response.getStatusCode(), response.getBody());
            throw new HttpClientErrorException(response.getStatusCode(), "Erro ao obter informações do usuário no ConectaID");
        }

        JsonNode body = response.getBody();
        return new AuthDTOs.UserInfoDTO(
                body.path("cpf").asText(null),
                body.path("name").asText(null),
                body.path("email").asText(null)
        );
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
}