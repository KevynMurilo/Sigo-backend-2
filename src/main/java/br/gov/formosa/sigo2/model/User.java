package br.gov.formosa.sigo2.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"name"})
public class User implements Serializable {

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
    private Role name;

    @Column(name = "doc_identidade_frente_url")
    private String identityDocumentFrontUrl;

    @Column(name = "doc_identidade_verso_url")
    private String identityDocumentBackUrl;

    @Column(name = "doc_comprovante_residencia_url")
    private String proofOfResidenceUrl;

    @Column(name = "onboarding_concluido", nullable = false)
    private boolean onboardingCompleted = false;
}