package br.gov.formosa.sigo2.repository;

import br.gov.formosa.sigo2.model.InspectionEvidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InspectionEvidenceRepository extends JpaRepository<InspectionEvidence, UUID> {
}
