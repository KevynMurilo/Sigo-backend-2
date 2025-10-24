package br.gov.formosa.sigo2.mapper;

import br.gov.formosa.sigo2.dto.RequestDTOs;
import br.gov.formosa.sigo2.model.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    RequestDTOs.PaymentSummaryDTO toPaymentSummaryDTO(Payment payment);
}