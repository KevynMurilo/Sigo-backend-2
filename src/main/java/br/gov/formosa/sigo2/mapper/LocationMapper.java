package br.gov.formosa.sigo2.mapper;

import br.gov.formosa.sigo2.dto.RequestDTOs;
import br.gov.formosa.sigo2.model.Location;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    RequestDTOs.LocationDTO toLocationDTO(Location location);
}