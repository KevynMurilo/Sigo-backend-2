package br.gov.formosa.sigo2.service.inspection;

import br.gov.formosa.sigo2.dto.InspectionDTOs;
import br.gov.formosa.sigo2.mapper.InspectionMapper;
import br.gov.formosa.sigo2.model.Inspection;
import br.gov.formosa.sigo2.model.User;
import br.gov.formosa.sigo2.model.enums.InspectionStatus;
import br.gov.formosa.sigo2.repository.InspectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListMyPendingInspectionsUseCase {

    private final InspectionRepository inspectionRepository;
    private final InspectionMapper inspectionMapper;

    @Transactional(readOnly = true)
    public Page<InspectionDTOs.InspectionSummaryDTO> execute(User inspector, Pageable pageable) {
        Page<Inspection> inspections = inspectionRepository.findByInspectorAndStatus(
                inspector,
                InspectionStatus.PENDENTE,
                pageable
        );
        return inspectionMapper.toInspectionSummaryDTOPage(inspections);
    }
}