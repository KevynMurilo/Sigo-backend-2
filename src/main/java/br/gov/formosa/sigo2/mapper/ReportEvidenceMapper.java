package br.gov.formosa.sigo2.mapper;

import br.gov.formosa.sigo2.dto.ReportDTOs;
import br.gov.formosa.sigo2.model.ReportEvidence;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReportEvidenceMapper {

    ReportDTOs.ReportEvidenceDTO toReportEvidenceDTO(ReportEvidence evidence);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "report", ignore = true)
    ReportEvidence fromFileUrl(String fileUrl);
}