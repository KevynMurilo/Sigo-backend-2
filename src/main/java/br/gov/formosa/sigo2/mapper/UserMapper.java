package br.gov.formosa.sigo2.mapper;

import br.gov.formosa.sigo2.dto.UserDTOs;
import br.gov.formosa.sigo2.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "role.name", target = "roleName")
    UserDTOs.UserResponseDTO toUserResponseDTO(User user);

    List<UserDTOs.UserResponseDTO> toUserResponseDTOList(List<User> users);

    default Page<UserDTOs.UserResponseDTO> toUserResponseDTOPage(Page<User> page) {
        return page.map(this::toUserResponseDTO);
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "onboardingCompleted", ignore = true)
    @Mapping(target = "identityDocumentFrontUrl", ignore = true)
    @Mapping(target = "identityDocumentBackUrl", ignore = true)
    @Mapping(target = "proofOfResidenceUrl", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    User adminCreateDTOToUser(UserDTOs.AdminCreateUserDTO dto);
}