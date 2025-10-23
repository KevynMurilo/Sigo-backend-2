package br.gov.formosa.sigo2.model;

import br.gov.formosa.sigo2.model.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "pagamento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"request"})
public class Payment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitacao_id", nullable = false)
    private Request request;

    @Column(name = "valor_total", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "data_vencimento")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_pagamento", nullable = false)
    private PaymentStatus status;

    @Column(name = "url_boleto")
    private String billUrl;

    @Column(name = "itens_pagamento", columnDefinition = "TEXT")
    private String paymentItemsJson;
}