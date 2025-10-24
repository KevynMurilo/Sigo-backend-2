package br.gov.formosa.sigo2.service.renewal;

import br.gov.formosa.sigo2.dto.PaymentDTOs;
import br.gov.formosa.sigo2.mapper.PaymentMapper;
import br.gov.formosa.sigo2.mapper.RequestMapper;
import br.gov.formosa.sigo2.model.Location;
import br.gov.formosa.sigo2.model.Payment;
import br.gov.formosa.sigo2.model.Request;
import br.gov.formosa.sigo2.model.enums.PaymentStatus;
import br.gov.formosa.sigo2.model.enums.RequestStatus;
import br.gov.formosa.sigo2.repository.RequestRepository;
import br.gov.formosa.sigo2.service.util.ConfigurationService;
import br.gov.formosa.sigo2.service.util.ProtocolGeneratorService;
import br.gov.formosa.sigo2.service.util.StatusHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateRenewalRequestUseCase {

    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final PaymentMapper paymentMapper;
    private final ProtocolGeneratorService protocolGeneratorService;
    private final StatusHistoryService statusHistoryService;
    private final ConfigurationService configurationService;

    @Transactional
    public Request execute(Request parentRequest) {
        log.info("Gerando renovação para a solicitação: {}", parentRequest.getProtocol());

        Request newRequest = requestMapper.createRenewalFromParent(parentRequest);

        newRequest.setProtocol(protocolGeneratorService.generate());
        newRequest.setCreatedAt(LocalDateTime.now());
        newRequest.setStatus(RequestStatus.AGUARDANDO_ACEITE_RENOVACAO);
        newRequest.setParentRequest(parentRequest);

        Location newLocation = new Location();
        newLocation.setRequest(newRequest);
        newLocation.setLatitude(parentRequest.getLocation().getLatitude());
        newLocation.setLongitude(parentRequest.getLocation().getLongitude());
        newRequest.setLocation(newLocation);

        Payment payment = createRenewalPayment(newRequest, parentRequest.getCommerceType());
        newRequest.setPayments(List.of(payment));

        Request savedRequest = requestRepository.save(newRequest);

        statusHistoryService.logStatusChange(savedRequest, null, RequestStatus.AGUARDANDO_ACEITE_RENOVACAO, null);

        log.info("Renovação {} criada com sucesso para a solicitação {}.", savedRequest.getProtocol(), parentRequest.getProtocol());
        return savedRequest;
    }

    private Payment createRenewalPayment(Request newRequest, String commerceType) {
        List<PaymentDTOs.FeeItemDTO> feeItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        BigDecimal taxaOcupacao = configurationService.getDecimal("TAXA_OCUPACAO_ANUAL_CHEIA");
        feeItems.add(new PaymentDTOs.FeeItemDTO("TAXA_OCUPACAO_ANUAL", taxaOcupacao));
        totalAmount = totalAmount.add(taxaOcupacao);

        if (configurationService.isSanitaryRequired(commerceType)) {
            BigDecimal taxaSanitaria = configurationService.getDecimal("TAXA_VISTORIA_SANITARIA_ANUAL");
            feeItems.add(new PaymentDTOs.FeeItemDTO("TAXA_VISTORIA_SANITARIA_ANUAL", taxaSanitaria));
            totalAmount = totalAmount.add(taxaSanitaria);
        }

        int daysToPay = configurationService.getInteger("RENEWAL_PAYMENT_DAYS");
        LocalDate dueDate = LocalDate.now().plusDays(daysToPay);

        Payment payment = paymentMapper.paymentFromFeeItems(feeItems, totalAmount, dueDate, null);
        payment.setRequest(newRequest);
        payment.setStatus(PaymentStatus.PENDENTE);

        return payment;
    }
}