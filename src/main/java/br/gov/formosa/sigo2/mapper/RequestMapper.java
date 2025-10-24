package br.gov.formosa.sigo2.mapper;

import br.gov.formosa.sigo2.dto.RequestDTOs;
import br.gov.formosa.sigo2.model.Request;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring", uses = {
        LocationMapper.class,
        InspectionMapper.class,
        PaymentMapper.class,
        StatusHistoryMapper.class
})
public interface RequestMapper {

    @Mapping(source = "applicant.fullName", target = "applicantName")
    @Mapping(source = "parentRequest.id", target = "parentRequestId")
    @Mapping(source = "photoFrontUrl", target = "photos.photoFrontUrl")
    @Mapping(source = "photoLeftUrl", target = "photos.photoLeftUrl")
    @Mapping(source = "photoRightUrl", target = "photos.photoRightUrl")
    @Mapping(source = "photoBackUrl", target = "photos.photoBackUrl")
    @Mapping(source = "cnpjDocumentUrl", target = "photos.cnpjDocumentUrl")
    RequestDTOs.RequestDetailsDTO toRequestDetailsDTO(Request request);

    RequestDTOs.RequestSummaryDTO toRequestSummaryDTO(Request request);

    default Page<RequestDTOs.RequestSummaryDTO> toRequestSummaryDTOPage(Page<Request> page) {
        return page.map(this::toRequestSummaryDTO);
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "protocol", ignore = true)
    @Mapping(target = "applicant", ignore = true)
    @Mapping(target = "areaSqM", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "expiresAt", ignore = true)
    @Mapping(target = "parentRequest", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "inspections", ignore = true)
    @Mapping(target = "payments", ignore = true)
    @Mapping(target = "statusHistory", ignore = true)
    @Mapping(target = "ownerDocument", source = "ownerDocument")
    Request createNewRequestDtoToRequest(RequestDTOs.CreateNewRequestDTO dto);
}