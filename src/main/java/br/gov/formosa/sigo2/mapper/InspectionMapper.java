package br.gov.formosa.sigo2.mapper;

import br.gov.formosa.sigo2.dto.RequestDTOs;
import br.gov.formosa.sigo2.model.Inspection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InspectionMapper {
    @Mapping(source = "inspector.fullName", target = "inspectorName")
    RequestDTOs.InspectionSummaryDTO toInspectionSummaryDTO(Inspection inspection);
}