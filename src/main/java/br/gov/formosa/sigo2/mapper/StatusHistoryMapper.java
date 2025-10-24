package br.gov.formosa.sigo2.mapper;

import br.gov.formosa.sigo2.dto.RequestDTOs;
import br.gov.formosa.sigo2.model.StatusHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StatusHistoryMapper {
    @Mapping(source = "responsibleUser.fullName", target = "responsibleUserName")
    RequestDTOs.StatusHistoryDTO toStatusHistoryDTO(StatusHistory statusHistory);
}