package br.gov.formosa.sigo2.service.payment;

import br.gov.formosa.sigo2.dto.PaymentDTOs;
import br.gov.formosa.sigo2.mapper.PaymentMapper;
import br.gov.formosa.sigo2.model.Inspection;
import br.gov.formosa.sigo2.model.Payment;
import br.gov.formosa.sigo2.model.Request;
import br.gov.formosa.sigo2.model.User;
import br.gov.formosa.sigo2.model.enums.PaymentStatus;
import br.gov.formosa.sigo2.model.enums.RequestStatus;
import br.gov.formosa.sigo2.repository.PaymentRepository;
import br.gov.formosa.sigo2.repository.RequestRepository;
import br.gov.formosa.sigo2.service.util.StatusHistoryService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GenerateConsolidatedBillUseCase {

    private final RequestRepository requestRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final StatusHistoryService statusHistoryService;

    @Transactional
    public PaymentDTOs.PaymentDetailsDTO execute(UUID requestId, PaymentDTOs.GenerateBillRequestDTO dto, User adminUser) {

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação não encontrada: " + requestId));

        if (request.getStatus() != RequestStatus.AGUARDANDO_EMISSAO_BOLETO) {
            throw new ValidationException("Solicitação não está aguardando emissão de boleto.");
        }

        List<Inspection> inspections = request.getInspections();
        if (inspections.isEmpty()) {
            throw new ValidationException("Solicitação não possui vistorias para gerar cobrança.");
        }

        BigDecimal totalAmount = inspections.stream()
                .map(Inspection::getCalculatedFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Payment payment = paymentMapper.paymentFromInspections(
                inspections,
                totalAmount,
                dto.dueDate(),
                dto.billUrl()
        );
        payment.setRequest(request);
        payment.setStatus(PaymentStatus.PENDENTE);

        Payment savedPayment = paymentRepository.save(payment);

        request.getPayments().add(savedPayment);
        request.setStatus(RequestStatus.AGUARDANDO_PAGAMENTO);
        requestRepository.save(request);

        statusHistoryService.logStatusChange(
                request,
                RequestStatus.AGUARDANDO_EMISSAO_BOLETO,
                RequestStatus.AGUARDANDO_PAGAMENTO,
                adminUser
        );

        return paymentMapper.toPaymentDetailsDTO(savedPayment);
    }
}