package br.gov.formosa.sigo2.mapper;

import br.gov.formosa.sigo2.dto.PaymentDTOs;
import br.gov.formosa.sigo2.model.Inspection;
import br.gov.formosa.sigo2.model.Payment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {ObjectMapper.class, TypeReference.class, IOException.class, JsonProcessingException.class})
public abstract class PaymentMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mapping(source = "id", target = "paymentId")
    @Mapping(source = "request.id", target = "requestId")
    @Mapping(target = "items", expression = "java(jsonToItems(payment.getPaymentItemsJson()))")
    public abstract PaymentDTOs.PaymentDetailsDTO toPaymentDetailsDTO(Payment payment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "request", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "paymentItemsJson", expression = "java(itemsToJson(inspections))")
    public abstract Payment paymentFromInspections(List<Inspection> inspections, BigDecimal totalAmount, LocalDate dueDate, String billUrl);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "request", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "paymentItemsJson", expression = "java(feeItemsToJson(feeItems))")
    public abstract Payment paymentFromFeeItems(List<PaymentDTOs.FeeItemDTO> feeItems, BigDecimal totalAmount, LocalDate dueDate, String billUrl);

    protected String itemsToJson(List<Inspection> inspections) {
        if (inspections == null || inspections.isEmpty()) {
            return "[]";
        }
        List<PaymentDTOs.FeeItemDTO> items = inspections.stream()
                .map(insp -> new PaymentDTOs.FeeItemDTO(
                        insp.getType().name(),
                        insp.getCalculatedFee()))
                .collect(Collectors.toList());

        return feeItemsToJson(items);
    }

    protected String feeItemsToJson(List<PaymentDTOs.FeeItemDTO> items) {
        try {
            return objectMapper.writeValueAsString(items);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Falha ao serializar itens do pagamento", e);
        }
    }

    protected List<PaymentDTOs.FeeItemDTO> jsonToItems(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<PaymentDTOs.FeeItemDTO>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Falha ao deserializar itens do pagamento", e);
        }
    }
}