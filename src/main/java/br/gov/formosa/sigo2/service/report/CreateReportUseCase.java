package br.gov.formosa.sigo2.service.report;

import br.gov.formosa.sigo2.dto.ReportDTOs;
import br.gov.formosa.sigo2.mapper.ReportMapper;
import br.gov.formosa.sigo2.model.Report;
import br.gov.formosa.sigo2.model.ReportEvidence;
import br.gov.formosa.sigo2.model.User;
import br.gov.formosa.sigo2.model.enums.ReportStatus;
import br.gov.formosa.sigo2.repository.ReportRepository;
import br.gov.formosa.sigo2.service.util.ProtocolGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreateReportUseCase {

    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;
    private final ProtocolGeneratorService protocolGeneratorService;

    @Transactional
    public ReportDTOs.ReportDetailsDTO execute(ReportDTOs.CreateReportDTO dto, User reporter) {

        Report report = reportMapper.fromCreateDTO(dto);
        report.setProtocol(protocolGeneratorService.generate());
        report.setStatus(ReportStatus.RECEBIDA);

        if (reporter != null && !dto.anonymous()) {
            report.setReporter(reporter);
            report.setAnonymous(false);
        } else {
            report.setAnonymous(true);
        }

        if (dto.evidenceFileUrls() != null && !dto.evidenceFileUrls().isEmpty()) {
            List<ReportEvidence> evidenceList = dto.evidenceFileUrls().stream()
                    .map(url -> {
                        ReportEvidence evidence = new ReportEvidence();
                        evidence.setFileUrl(url);
                        evidence.setReport(report);
                        return evidence;
                    })
                    .collect(Collectors.toList());
            report.setEvidence(evidenceList);
        }

        Report savedReport = reportRepository.save(report);
        return reportMapper.toReportDetailsDTO(savedReport);
    }
}