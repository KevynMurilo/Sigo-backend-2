package br.gov.formosa.sigo2.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"role"})
public class User implements Serializable, UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(name = "nome", nullable = false)
    private String fullName;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "cpf", unique = true, nullable = false)
    private String cpf;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "papel_id")
    private Role role;

    @Column(name = "doc_identidade_frente_url")
    private String identityDocumentFrontUrl;

    @Column(name = "doc_identidade_verso_url")
    private String identityDocumentBackUrl;

    @Column(name = "doc_comprovante_residencia_url")
    private String proofOfResidenceUrl;

    @Column(name = "onboarding_concluido", nullable = false)
    private boolean onboardingCompleted = false;

    @Column(name = "ativo")
    private boolean enabled = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(() -> "ROLE_" + getRole().getName());
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled();
    }
}