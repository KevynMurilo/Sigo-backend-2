package br.gov.formosa.sigo2.service.request;

import br.gov.formosa.sigo2.dto.RequestDTOs;
import br.gov.formosa.sigo2.mapper.RequestMapper;
import br.gov.formosa.sigo2.model.Location;
import br.gov.formosa.sigo2.model.Request;
import br.gov.formosa.sigo2.model.User;
import br.gov.formosa.sigo2.model.enums.OwnerType;
import br.gov.formosa.sigo2.model.enums.RequestStatus;
import br.gov.formosa.sigo2.repository.RequestRepository;
import br.gov.formosa.sigo2.service.util.ProtocolGeneratorService;
import br.gov.formosa.sigo2.service.util.StatusHistoryService;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateNewRequestUseCase {

    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final ProtocolGeneratorService protocolGeneratorService;
    private final StatusHistoryService statusHistoryService;

    @Transactional
    public RequestDTOs.RequestDetailsDTO execute(RequestDTOs.CreateNewRequestDTO dto, User applicant) {

        validateInput(dto, applicant);

        Request request = requestMapper.createNewRequestDtoToRequest(dto);
        request.setApplicant(applicant);
        request.setProtocol(protocolGeneratorService.generate());
        request.setCreatedAt(LocalDateTime.now());
        request.setStatus(RequestStatus.NOVA);

        if (dto.ownerType() == OwnerType.CPF) {
            request.setOwnerDocument(applicant.getCpf());
        }

        BigDecimal area = dto.areaWidth().multiply(dto.areaLength());
        request.setAreaSqM(area);

        Location location = new Location();
        location.setLatitude(dto.latitude());
        location.setLongitude(dto.longitude());
        location.setRequest(request);
        request.setLocation(location);

        Request savedRequest = requestRepository.save(request);

        statusHistoryService.logStatusChange(savedRequest, null, RequestStatus.NOVA, applicant);

        return requestMapper.toRequestDetailsDTO(savedRequest);
    }

    private void validateInput(RequestDTOs.CreateNewRequestDTO dto, User applicant) {
        if (dto.ownerType() == OwnerType.CNPJ) {
            if (dto.ownerDocument() == null || dto.ownerDocument().isBlank()) {
                throw new ValidationException("CNPJ é obrigatório para tipo Pessoa Jurídica.");
            }
            if (dto.cnpjDocumentUrl() == null || dto.cnpjDocumentUrl().isBlank()) {
                throw new ValidationException("Cartão CNPJ é obrigatório para tipo Pessoa Jurídica.");
            }
        }
        if (!applicant.isOnboardingCompleted()) {
            throw new ValidationException("Usuário deve completar o onboarding pessoal primeiro.");
        }
    }
}