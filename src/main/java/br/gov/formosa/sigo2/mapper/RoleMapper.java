package br.gov.formosa.sigo2.mapper;

import br.gov.formosa.sigo2.dto.RoleDTOs;
import br.gov.formosa.sigo2.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleDTOs.RoleResponseDTO toRoleResponseDTO(Role role);

    List<RoleDTOs.RoleResponseDTO> toRoleResponseDTOList(List<Role> roles);

    @Mapping(target = "id", ignore = true)
    Role fromCreateDTO(RoleDTOs.CreateRoleDTO dto);

    @Mapping(target = "id", ignore = true)
    void updateFromDTO(RoleDTOs.UpdateRoleDTO dto, @MappingTarget Role role);
}