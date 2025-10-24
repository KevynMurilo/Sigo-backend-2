package br.gov.formosa.sigo2.dto;

import br.gov.formosa.sigo2.model.enums.PaymentStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class PaymentDTOs {

    public record FeeItemDTO(
            String description,
            BigDecimal amount
    ) {}

    public record GenerateBillRequestDTO(
            @NotNull
            @Future
            LocalDate dueDate,

            @NotBlank
            String billUrl
    ) {}

    public record PaymentDetailsDTO(
            UUID paymentId,
            UUID requestId,
            BigDecimal totalAmount,
            LocalDate dueDate,
            PaymentStatus status,
            String billUrl,
            List<FeeItemDTO> items
    ) {}

}