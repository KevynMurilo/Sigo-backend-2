package br.gov.formosa.sigo2.mapper;

import br.gov.formosa.sigo2.dto.InspectionDTOs;
import br.gov.formosa.sigo2.model.ChecklistTemplateItem;
import br.gov.formosa.sigo2.model.Inspection;
import br.gov.formosa.sigo2.model.InspectionChecklistItem;
import br.gov.formosa.sigo2.model.InspectionEvidence;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface InspectionMapper {

    @Mapping(source = "id", target = "inspectionId")
    @Mapping(source = "request.id", target = "requestId")
    @Mapping(source = "request.protocol", target = "requestProtocol")
    @Mapping(source = "request.tradeName", target = "requestTradeName")
    InspectionDTOs.InspectionSummaryDTO toInspectionSummaryDTO(Inspection inspection);

    default Page<InspectionDTOs.InspectionSummaryDTO> toInspectionSummaryDTOPage(Page<Inspection> page) {
        return page.map(this::toInspectionSummaryDTO);
    }

    @Mapping(source = "id", target = "templateItemId")
    InspectionDTOs.ChecklistQuestionDTO toChecklistQuestionDTO(ChecklistTemplateItem templateItem);

    @Mapping(source = "id", target = "inspectionId")
    @Mapping(source = "request.id", target = "requestId")
    @Mapping(source = "request.protocol", target = "requestProtocol")
    @Mapping(source = "request.tradeName", target = "requestTradeName")
    @Mapping(source = "request.location.latitude", target = "requestLocation.latitude")
    @Mapping(source = "request.location.longitude", target = "requestLocation.longitude")
    @Mapping(target = "checklistQuestions", ignore = true)
    InspectionDTOs.InspectionDetailsDTO toInspectionDetailsDTO(Inspection inspection);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "inspection", ignore = true)
    @Mapping(target = "questionText", ignore = true)
    @Mapping(target = "questionType", ignore = true)
    @Mapping(target = "itemOrder", ignore = true)
    @Mapping(target = "optionsJson", ignore = true)
    @Mapping(target = "observation", source = "observation")
    InspectionChecklistItem submittedAnswerToItem(InspectionDTOs.SubmittedAnswerDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "inspection", ignore = true)
    InspectionEvidence submittedEvidenceToEvidence(InspectionDTOs.SubmittedEvidenceDTO dto);

    @Mapping(source = "id", target = "id")
    InspectionDTOs.InspectionChecklistItemResponseDTO toChecklistItemResponseDTO(InspectionChecklistItem item);

    @Mapping(source = "id", target = "id")
    InspectionDTOs.InspectionEvidenceResponseDTO toEvidenceResponseDTO(InspectionEvidence evidence);

    @Mapping(source = "request.id", target = "requestId")
    @Mapping(source = "inspector.fullName", target = "inspectorName")
    InspectionDTOs.FullInspectionResponseDTO toFullInspectionResponseDTO(Inspection inspection);
}