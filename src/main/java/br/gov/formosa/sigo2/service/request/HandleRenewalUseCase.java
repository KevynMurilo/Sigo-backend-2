package br.gov.formosa.sigo2.service.request;

import br.gov.formosa.sigo2.dto.RequestDTOs;
import br.gov.formosa.sigo2.mapper.RequestMapper;
import br.gov.formosa.sigo2.model.Request;
import br.gov.formosa.sigo2.model.User;
import br.gov.formosa.sigo2.model.enums.RequestStatus;
import br.gov.formosa.sigo2.repository.RequestRepository;
import br.gov.formosa.sigo2.service.util.StatusHistoryService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HandleRenewalUseCase {

    private final RequestRepository requestRepository;
    private final StatusHistoryService statusHistoryService;
    private final RequestMapper requestMapper;

    @Transactional
    public RequestDTOs.RequestDetailsDTO accept(UUID requestId, User applicant) {
        Request request = findAndValidateRenewal(requestId, applicant);

        request.setStatus(RequestStatus.AGUARDANDO_PAGAMENTO);
        Request savedRequest = requestRepository.save(request);

        statusHistoryService.logStatusChange(savedRequest, RequestStatus.AGUARDANDO_ACEITE_RENOVACAO, RequestStatus.AGUARDANDO_PAGAMENTO, applicant);

        return requestMapper.toRequestDetailsDTO(savedRequest);
    }

    @Transactional
    public RequestDTOs.RequestDetailsDTO reject(UUID requestId, User applicant) {
        Request request = findAndValidateRenewal(requestId, applicant);

        request.setStatus(RequestStatus.INATIVO);
        Request savedRequest = requestRepository.save(request);

        statusHistoryService.logStatusChange(savedRequest, RequestStatus.AGUARDANDO_ACEITE_RENOVACAO, RequestStatus.INATIVO, applicant);

        return requestMapper.toRequestDetailsDTO(savedRequest);
    }

    private Request findAndValidateRenewal(UUID requestId, User applicant) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação de renovação não encontrada."));

        if (!request.getApplicant().equals(applicant)) {
            throw new AccessDeniedException("Usuário não é o dono desta solicitação.");
        }

        if (request.getStatus() != RequestStatus.AGUARDANDO_ACEITE_RENOVACAO) {
            throw new ValidationException("Esta solicitação não está aguardando aceite de renovação.");
        }
        return request;
    }
}