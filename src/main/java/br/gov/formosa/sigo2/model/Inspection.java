package br.gov.formosa.sigo2.model;

import br.gov.formosa.sigo2.model.enums.InspectionStatus;
import br.gov.formosa.sigo2.model.enums.InspectionType;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "vistoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"request", "inspector", "evidence", "inspectionItems"})
public class Inspection implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitacao_id", nullable = false)
    private Request request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_responsavel_id")
    private User inspector;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_vistoria", nullable = false)
    private InspectionType type;

    @Column(name = "data_vistoria")
    private LocalDateTime inspectionDate;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observations;

    @Column(name = "valor_taxa_calculada")
    private BigDecimal calculatedFee;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_vistoria", nullable = false)
    private InspectionStatus status;

    @OneToMany(mappedBy = "inspection", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InspectionEvidence> evidence;

    @OneToMany(mappedBy = "inspection", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("itemOrder ASC")
    private List<InspectionChecklistItem> inspectionItems;
}