package br.gov.formosa.sigo2.model;

import br.gov.formosa.sigo2.model.enums.ChecklistItemType;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "vistoria_checklist_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"inspection"})
public class InspectionChecklistItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vistoria_id", nullable = false)
    private Inspection inspection;

    @Column(name = "texto_pergunta", nullable = false)
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pergunta", nullable = false)
    private ChecklistItemType questionType;

    @Column(name = "ordem_item", nullable = false)
    private int itemOrder;

    @Column(name = "opcoes_json", columnDefinition = "TEXT")
    private String optionsJson;

    @Column(name = "valor_resposta", columnDefinition = "TEXT")
    private String answerValue;

    @Column(name = "observacao_item")
    private String observation;
}