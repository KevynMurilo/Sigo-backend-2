package br.gov.formosa.sigo2.config;

import br.gov.formosa.sigo2.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // Import HttpMethod
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final String SOLICITANTE = "SOLICITANTE";
    private static final String CIDADAO_DENUNCIANTE = "CIDADAO_DENUNCIANTE";
    private static final String SECRETARIO = "SECRETARIO";
    private static final String FISCAL = "FISCAL";
    private static final String VIGILANTE_SANITARIO = "VIGILANTE_SANITARIO";
    private static final String ADMINISTRATIVO = "ADMINISTRATIVO";
    private static final String ADMIN_MASTER = "ADMIN_MASTER";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)) // Para console H2
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // API stateless
                .authorizeHttpRequests(auth -> auth
                        // === ROTAS PÚBLICAS ===
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/api-docs/**").permitAll() // Documentação Swagger
                        .requestMatchers(HttpMethod.GET, "/api/auth/conectaid/callback").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/conectaid/simulate").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/reports").permitAll()

                        // === ROTAS AUTENTICADAS (QUALQUER PAPEL LOGADO) ===
                        .requestMatchers(HttpMethod.GET, "/api/users/me").authenticated()

                        // === ROTAS DO SOLICITANTE ===
                        .requestMatchers(HttpMethod.POST, "/api/users/me/onboarding").hasRole(SOLICITANTE)
                        .requestMatchers(HttpMethod.POST, "/api/requests").hasRole(SOLICITANTE)
                        .requestMatchers(HttpMethod.GET, "/api/requests/my").hasRole(SOLICITANTE)
                        .requestMatchers(HttpMethod.POST, "/api/requests/{id}/submit-correction").hasRole(SOLICITANTE)
                        .requestMatchers(HttpMethod.POST, "/api/requests/{id}/renewal/accept").hasRole(SOLICITANTE)
                        .requestMatchers(HttpMethod.POST, "/api/requests/{id}/renewal/reject").hasRole(SOLICITANTE)
                        // GET /api/requests/{id} é mais complexo, melhor @PreAuthorize ou lógica no serviço (permitido se dono)

                        // === ROTAS DO CIDADAO_DENUNCIANTE ===
                        // Qualquer logado pode ver as suas
                        .requestMatchers(HttpMethod.GET, "/api/reports/my-submitted").hasAnyRole(CIDADAO_DENUNCIANTE, SOLICITANTE, SECRETARIO, FISCAL, VIGILANTE_SANITARIO, ADMINISTRATIVO, ADMIN_MASTER)
                        // GET /api/reports/{id} também precisa de @PreAuthorize ou lógica no serviço (permitido se dono não anônimo)

                        // === ROTAS DO SECRETARIO ===
                        .requestMatchers(HttpMethod.GET, "/api/requests").hasAnyRole(SECRETARIO, ADMINISTRATIVO, ADMIN_MASTER)
                        .requestMatchers(HttpMethod.POST, "/api/requests/{id}/triage").hasRole(SECRETARIO)
                        .requestMatchers(HttpMethod.GET, "/api/reports/triage").hasRole(SECRETARIO)
                        .requestMatchers(HttpMethod.POST, "/api/reports/{id}/assign").hasRole(SECRETARIO)

                        // === ROTAS DO FISCAL / VIGILANTE SANITARIO ===
                        .requestMatchers(HttpMethod.GET, "/api/inspections/my-pending").hasAnyRole(FISCAL, VIGILANTE_SANITARIO)
                        .requestMatchers(HttpMethod.GET, "/api/inspections/{id}").hasAnyRole(FISCAL, VIGILANTE_SANITARIO)
                        .requestMatchers(HttpMethod.POST, "/api/inspections/{id}/approve").hasAnyRole(FISCAL, VIGILANTE_SANITARIO)
                        .requestMatchers(HttpMethod.POST, "/api/inspections/{id}/request-correction").hasRole(VIGILANTE_SANITARIO)
                        .requestMatchers(HttpMethod.GET, "/api/reports/my-pending").hasAnyRole(FISCAL, VIGILANTE_SANITARIO)
                        .requestMatchers(HttpMethod.POST, "/api/reports/{id}/resolve").hasAnyRole(FISCAL, VIGILANTE_SANITARIO)

                        // === ROTAS DO ADMINISTRATIVO ===
                        .requestMatchers(HttpMethod.POST, "/api/payments/generate-bill/request/{requestId}").hasRole(ADMINISTRATIVO)
                        .requestMatchers(HttpMethod.POST, "/api/payments/{paymentId}/register-manual-payment").hasRole(ADMINISTRATIVO)

                        // === ROTAS DO ADMIN_MASTER ===
                        // Users
                        .requestMatchers(HttpMethod.GET, "/api/users").hasRole(ADMIN_MASTER)
                        .requestMatchers(HttpMethod.POST, "/api/users").hasRole(ADMIN_MASTER)
                        .requestMatchers(HttpMethod.GET, "/api/users/{id}").hasRole(ADMIN_MASTER)
                        .requestMatchers(HttpMethod.PUT, "/api/users/{id}/role").hasRole(ADMIN_MASTER)
                        .requestMatchers(HttpMethod.DELETE, "/api/users/{id}").hasRole(ADMIN_MASTER)
                        // Roles
                        .requestMatchers("/api/roles/**").hasRole(ADMIN_MASTER)
                        // Templates
                        .requestMatchers("/api/admin/templates/**").hasRole(ADMIN_MASTER)
                        // Configurations
                        .requestMatchers("/api/admin/configurations/**").hasRole(ADMIN_MASTER)

                        // === ROTAS COM PERMISSÕES MAIS COMPLEXAS (Usar @PreAuthorize ou lógica no serviço se necessário) ===
                        // GET /api/requests/{id} (Solicitante dono, Admin, Secretario, Atribuídos) - A lógica atual no UseCase está OK.
                        // GET /api/reports/{id} (Reportador não anônimo, Admin, Secretario, Atribuído) - A lógica atual no UseCase está OK.

                        // === QUALQUER OUTRA ROTA ===
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200", "https://sigo.formosa.go.gov.br"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}