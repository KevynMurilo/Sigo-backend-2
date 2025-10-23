package br.gov.formosa.sigo2.repository;

import br.gov.formosa.sigo2.model.ReportEvidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<ReportEvidence, UUID> {
}
