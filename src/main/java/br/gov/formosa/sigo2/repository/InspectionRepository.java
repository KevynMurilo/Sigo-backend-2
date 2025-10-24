package br.gov.formosa.sigo2.repository;

import br.gov.formosa.sigo2.model.Inspection;
import br.gov.formosa.sigo2.model.User;
import br.gov.formosa.sigo2.model.enums.InspectionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InspectionRepository extends JpaRepository<Inspection, UUID> {

    Page<Inspection> findByInspectorAndStatus(User inspector, InspectionStatus status, Pageable pageable);

    Optional<Inspection> findByIdAndInspector(UUID id, User inspector);
}