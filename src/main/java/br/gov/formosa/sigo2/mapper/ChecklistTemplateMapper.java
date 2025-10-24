package br.gov.formosa.sigo2.mapper;

import br.gov.formosa.sigo2.dto.TemplateDTOs;
import br.gov.formosa.sigo2.model.ChecklistTemplate;
import br.gov.formosa.sigo2.model.ChecklistTemplateItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChecklistTemplateMapper {

    @Mapping(source = "id", target = "id")
    TemplateDTOs.ChecklistItemDTO toChecklistItemDTO(ChecklistTemplateItem item);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "template", ignore = true)
    ChecklistTemplateItem fromCreateChecklistItemDTO(TemplateDTOs.CreateChecklistItemDTO dto);

    List<ChecklistTemplateItem> fromCreateChecklistItemDTOList(List<TemplateDTOs.CreateChecklistItemDTO> dtoList);

    @Mapping(source = "items", target = "itemCount", qualifiedByName = "countItems")
    TemplateDTOs.ChecklistTemplateSummaryDTO toTemplateSummaryDTO(ChecklistTemplate template);

    default Page<TemplateDTOs.ChecklistTemplateSummaryDTO> toTemplateSummaryDTOPage(Page<ChecklistTemplate> page) {
        return page.map(this::toTemplateSummaryDTO);
    }

    @Named("countItems")
    default int countItems(List<ChecklistTemplateItem> items) {
        return items != null ? items.size() : 0;
    }

    TemplateDTOs.ChecklistTemplateDetailsDTO toTemplateDetailsDTO(ChecklistTemplate template);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    ChecklistTemplate fromCreateTemplateDTO(TemplateDTOs.CreateChecklistTemplateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "items", ignore = true)
    void updateTemplateFromDTO(TemplateDTOs.UpdateChecklistTemplateDTO dto, @MappingTarget ChecklistTemplate template);

}