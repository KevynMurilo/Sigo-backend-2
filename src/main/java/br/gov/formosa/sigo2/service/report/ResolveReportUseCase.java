package br.gov.formosa.sigo2.service.report;

import br.gov.formosa.sigo2.dto.ReportDTOs;
import br.gov.formosa.sigo2.mapper.ReportMapper;
import br.gov.formosa.sigo2.model.Report;
import br.gov.formosa.sigo2.model.User;
import br.gov.formosa.sigo2.model.enums.ReportStatus;
import br.gov.formosa.sigo2.repository.ReportRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResolveReportUseCase {

    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;

    @Transactional
    public ReportDTOs.ReportDetailsDTO execute(UUID reportId, ReportDTOs.ResolveReportDTO dto, User inspector) {

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Denúncia não encontrada: " + reportId));

        if (report.getStatus() != ReportStatus.ENCAMINHADA) {
            throw new ValidationException("Esta denúncia não está aguardando resolução.");
        }

        if (!report.getAssignedTo().equals(inspector)) {
            throw new AccessDeniedException("Usuário não é o responsável por esta denúncia.");
        }

        String resolution = "\n\n--- RESOLUÇÃO DA EQUIPE ---\n" + dto.resultNotes();
        report.setDescription(report.getDescription() + resolution);
        report.setStatus(ReportStatus.RESOLVIDA);

        Report savedReport = reportRepository.save(report);
        return reportMapper.toReportDetailsDTO(savedReport);
    }
}