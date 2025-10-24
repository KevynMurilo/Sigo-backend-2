package br.gov.formosa.sigo2.service.request;

import br.gov.formosa.sigo2.dto.RequestDTOs;
import br.gov.formosa.sigo2.mapper.RequestMapper;
import br.gov.formosa.sigo2.model.Request;
import br.gov.formosa.sigo2.model.User;
import br.gov.formosa.sigo2.repository.RequestRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FindRequestDetailsUseCase {

    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;

    @Transactional(readOnly = true)
    public RequestDTOs.RequestDetailsDTO execute(UUID requestId, User currentUser) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação não encontrada: " + requestId));

        // TODO: Implementar Role-based check
        boolean isOwner = request.getApplicant().equals(currentUser);
        boolean isAdmin = currentUser.getRole().getName().equals("ADMIN_MASTER");
        boolean isStaff = Set.of("SECRETARIO", "FISCAL", "VIGILANTE_SANITARIO", "ADMINISTRATIVO")
                .contains(currentUser.getRole().getName());

        if (!isOwner && !isAdmin && !isStaff) {
            throw new AccessDeniedException("Acesso negado a esta solicitação.");
        }

        // Otimização: carregar coleções lazy
        request.getStatusHistory().size();
        request.getInspections().size();
        request.getPayments().size();

        return requestMapper.toRequestDetailsDTO(request);
    }
}