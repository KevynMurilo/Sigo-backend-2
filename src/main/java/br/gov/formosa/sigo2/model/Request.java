package br.gov.formosa.sigo2.model;

import br.gov.formosa.sigo2.model.enums.RequestStatus;
import br.gov.formosa.sigo2.model.enums.OwnerType;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "solicitacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"applicant", "parentRequest", "location", "inspections", "payments", "statusHistory"})
public class Request implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(name = "protocolo", unique = true, nullable = false)
    private String protocol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_solicitante_id", nullable = false)
    private User applicant;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_responsavel", nullable = false)
    private OwnerType ownerType;

    @Column(name = "documento_responsavel", nullable = false)
    private String ownerDocument;

    @Column(name = "razao_social")
    private String companyName;

    @Column(name = "nome_estabelecimento")
    private String tradeName;

    @Column(name = "tipo_comercio")
    private String commerceType;

    @Column(name = "area_m2")
    private BigDecimal areaSqM;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RequestStatus status;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "data_vigencia_fim")
    private LocalDate expiresAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitacao_pai_id")
    private Request parentRequest;

    @Column(name = "doc_foto_frente_url")
    private String photoFrontUrl;

    @Column(name = "doc_foto_lateral_esq_url")
    private String photoLeftUrl;

    @Column(name = "doc_foto_lateral_dir_url")
    private String photoRightUrl;

    @Column(name = "doc_foto_fundos_url")
    private String photoBackUrl;

    @Column(name = "doc_cartao_cnpj_url")
    private String cnpjDocumentUrl;

    @OneToOne(mappedBy = "request", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Location location;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Inspection> inspections;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StatusHistory> statusHistory;
}
