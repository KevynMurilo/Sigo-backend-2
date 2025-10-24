package br.gov.formosa.sigo2.mapper;

import br.gov.formosa.sigo2.dto.ReportDTOs;
import br.gov.formosa.sigo2.model.Report;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring", uses = {ReportEvidenceMapper.class})
public interface ReportMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "protocol", ignore = true)
    @Mapping(target = "reporter", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "assignedTo", ignore = true)
    @Mapping(target = "evidence", ignore = true)
    Report fromCreateDTO(ReportDTOs.CreateReportDTO dto);

    @Mapping(source = "reporter.fullName", target = "reporterName", qualifiedByName = "mapReporterName")
    @Mapping(source = "assignedTo.fullName", target = "assignedToName")
    ReportDTOs.ReportDetailsDTO toReportDetailsDTO(Report report);

    @Mapping(source = "assignedTo.fullName", target = "assignedToName")
    @Mapping(source = "description", target = "descriptionSnippet", qualifiedByName = "toSnippet")
    ReportDTOs.ReportSummaryDTO toReportSummaryDTO(Report report);

    default Page<ReportDTOs.ReportSummaryDTO> toReportSummaryDTOPage(Page<Report> page) {
        return page.map(this::toReportSummaryDTO);
    }

    @Named("mapReporterName")
    default String mapReporterName(String fullName) {
        return (fullName != null) ? fullName : "Anônimo";
    }

    @Named("toSnippet")
    default String toSnippet(String description) {
        if (description == null) return null;
        return description.length() > 100 ? description.substring(0, 100) + "..." : description;
    }
}