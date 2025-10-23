package br.gov.formosa.sigo2.model;

import br.gov.formosa.sigo2.model.enums.ChecklistItemType;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "checklist_template_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"template"})
public class ChecklistTemplateItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private ChecklistTemplate template;

    @Column(name = "texto_pergunta", nullable = false)
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pergunta", nullable = false)
    private ChecklistItemType questionType;

    @Column(name = "ordem_item", nullable = false)
    private int itemOrder;

    @Column(name = "obrigatorio", nullable = false)
    private boolean required = true;

    @Column(name = "opcoes_json", columnDefinition = "TEXT")
    private String optionsJson;
}