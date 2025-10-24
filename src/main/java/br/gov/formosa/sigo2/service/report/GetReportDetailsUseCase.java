package br.gov.formosa.sigo2.service.report;

import br.gov.formosa.sigo2.dto.ReportDTOs;
import br.gov.formosa.sigo2.mapper.ReportMapper;
import br.gov.formosa.sigo2.model.Report;
import br.gov.formosa.sigo2.model.User;
import br.gov.formosa.sigo2.repository.ReportRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetReportDetailsUseCase {

    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;

    @Transactional(readOnly = true)
    public ReportDTOs.ReportDetailsDTO execute(UUID reportId, User currentUser) {

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Denúncia não encontrada: " + reportId));

        boolean isReporter = !report.isAnonymous() && report.getReporter().equals(currentUser);
        boolean isAssigned = report.getAssignedTo() != null && report.getAssignedTo().equals(currentUser);
        boolean isAdmin = Set.of("ADMIN_MASTER", "SECRETARIO").contains(currentUser.getRole().getName());

        if (!isReporter && !isAssigned && !isAdmin) {
            throw new AccessDeniedException("Acesso negado a esta denúncia.");
        }

        return reportMapper.toReportDetailsDTO(report);
    }
}