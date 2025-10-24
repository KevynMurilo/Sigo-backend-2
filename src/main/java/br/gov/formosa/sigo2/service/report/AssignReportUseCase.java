package br.gov.formosa.sigo2.service.report;

import br.gov.formosa.sigo2.dto.ReportDTOs;
import br.gov.formosa.sigo2.mapper.ReportMapper;
import br.gov.formosa.sigo2.model.Report;
import br.gov.formosa.sigo2.model.User;
import br.gov.formosa.sigo2.model.enums.ReportStatus;
import br.gov.formosa.sigo2.repository.ReportRepository;
import br.gov.formosa.sigo2.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssignReportUseCase {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ReportMapper reportMapper;

    @Transactional
    public ReportDTOs.ReportDetailsDTO execute(UUID reportId, ReportDTOs.AssignReportDTO dto, User secretario) {

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Denúncia não encontrada: " + reportId));

        if (report.getStatus() != ReportStatus.RECEBIDA) {
            throw new ValidationException("Esta denúncia não está aguardando triagem.");
        }

        User assignedUser = userRepository.findById(dto.assignToUserId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário de destino não encontrado."));

        report.setAssignedTo(assignedUser);
        report.setStatus(ReportStatus.ENCAMINHADA);

        Report savedReport = reportRepository.save(report);
        return reportMapper.toReportDetailsDTO(savedReport);
    }
}