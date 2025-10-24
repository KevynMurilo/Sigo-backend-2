package br.gov.formosa.sigo2.service.payment;

import br.gov.formosa.sigo2.dto.PaymentDTOs;
import br.gov.formosa.sigo2.mapper.PaymentMapper;
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

import java.time.LocalDate;
import java.time.Month;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterManualPaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final RequestRepository requestRepository;
    private final StatusHistoryService statusHistoryService;
    private final PaymentMapper paymentMapper;

    @Transactional
    public PaymentDTOs.PaymentDetailsDTO execute(UUID paymentId, User adminUser) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Pagamento não encontrado: " + paymentId));

        if (payment.getStatus() != PaymentStatus.PENDENTE) {
            throw new ValidationException("Este pagamento não está pendente.");
        }

        Request request = payment.getRequest();
        if (request.getStatus() != RequestStatus.AGUARDANDO_PAGAMENTO) {
            throw new ValidationException("A solicitação associada não está aguardando pagamento.");
        }

        payment.setStatus(PaymentStatus.PAGO);
        Payment savedPayment = paymentRepository.save(payment);

        request.setStatus(RequestStatus.ATIVO);
        request.setExpiresAt(getLicenseExpiryDate());
        requestRepository.save(request);

        statusHistoryService.logStatusChange(
                request,
                RequestStatus.AGUARDANDO_PAGAMENTO,
                RequestStatus.ATIVO,
                adminUser
        );

        return paymentMapper.toPaymentDetailsDTO(savedPayment);
    }

    private LocalDate getLicenseExpiryDate() {
        int currentYear = LocalDate.now().getYear();
        return LocalDate.of(currentYear, Month.DECEMBER, 31);
    }
}