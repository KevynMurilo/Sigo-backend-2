package br.gov.formosa.sigo2.service.request;

import br.gov.formosa.sigo2.dto.RequestDTOs;
import br.gov.formosa.sigo2.mapper.RequestMapper;
import br.gov.formosa.sigo2.model.Request;
import br.gov.formosa.sigo2.model.User;
import br.gov.formosa.sigo2.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListMyRequestsUseCase {

    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;

    @Transactional(readOnly = true)
    public Page<RequestDTOs.RequestSummaryDTO> execute(User applicant, Pageable pageable) {
        Page<Request> requests = requestRepository.findByApplicant(applicant, pageable);
        return requestMapper.toRequestSummaryDTOPage(requests);
    }
}