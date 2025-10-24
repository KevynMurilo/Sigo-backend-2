package br.gov.formosa.sigo2.mapper;

import br.gov.formosa.sigo2.dto.ConfigDTOs;
import br.gov.formosa.sigo2.model.Configuration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConfigurationMapper {

    ConfigDTOs.ConfigurationDTO toConfigurationDTO(Configuration configuration);

    List<ConfigDTOs.ConfigurationDTO> toConfigurationDTOList(List<Configuration> configurations);

    default Page<ConfigDTOs.ConfigurationDTO> toConfigurationDTOPage(Page<Configuration> page) {
        return page.map(this::toConfigurationDTO);
    }

    Configuration fromConfigurationDTO(ConfigDTOs.ConfigurationDTO dto);

    void updateFromDTO(ConfigDTOs.ConfigurationDTO dto, @MappingTarget Configuration configuration);
}