package br.gov.formosa.sigo2.mapper;

import br.gov.formosa.sigo2.dto.AuthDTOs;
import br.gov.formosa.sigo2.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.role.name", target = "roleName")
    @Mapping(target = "token", ignore = true)
    AuthDTOs.LoginResponseDTO toLoginResponseDTO(User user);
}