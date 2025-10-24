package br.gov.formosa.sigo2.service.report;

import br.gov.formosa.sigo2.dto.ReportDTOs;
import br.gov.formosa.sigo2.mapper.ReportMapper;
import br.gov.formosa.sigo2.model.Report;
import br.gov.formosa.sigo2.model.User;
import br.gov.formosa.sigo2.model.enums.ReportStatus;
import br.gov.formosa.sigo2.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListMyPendingReportsUseCase {

    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;

    @Transactional(readOnly = true)
    public Page<ReportDTOs.ReportSummaryDTO> execute(User inspector, Pageable pageable) {
        Page<Report> reports = reportRepository.findByAssignedToAndStatus(
                inspector,
                ReportStatus.ENCAMINHADA,
                pageable
        );
        return reportMapper.toReportSummaryDTOPage(reports);
    }
}