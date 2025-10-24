package br.gov.formosa.sigo2.controller;

import br.gov.formosa.sigo2.dto.PaymentDTOs;
import br.gov.formosa.sigo2.model.User;
import br.gov.formosa.sigo2.service.payment.GenerateConsolidatedBillUseCase;
import br.gov.formosa.sigo2.service.payment.RegisterManualPaymentUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final GenerateConsolidatedBillUseCase generateConsolidatedBillUseCase;
    private final RegisterManualPaymentUseCase registerManualPaymentUseCase;

    @PostMapping("/generate-bill/request/{requestId}")
    public ResponseEntity<PaymentDTOs.PaymentDetailsDTO> generateBill(
            @PathVariable UUID requestId,
            @Valid @RequestBody PaymentDTOs.GenerateBillRequestDTO dto,
            @AuthenticationPrincipal User adminUser) {

        PaymentDTOs.PaymentDetailsDTO payment = generateConsolidatedBillUseCase.execute(requestId, dto, adminUser);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/{paymentId}/register-manual-payment")
    public ResponseEntity<PaymentDTOs.PaymentDetailsDTO> registerPayment(
            @PathVariable UUID paymentId,
            @AuthenticationPrincipal User adminUser) {

        PaymentDTOs.PaymentDetailsDTO payment = registerManualPaymentUseCase.execute(paymentId, adminUser);
        return ResponseEntity.ok(payment);
    }
}