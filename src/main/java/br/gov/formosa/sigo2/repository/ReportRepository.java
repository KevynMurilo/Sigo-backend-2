package br.gov.formosa.sigo2.repository;

import br.gov.formosa.sigo2.model.Report;
import br.gov.formosa.sigo2.model.User;
import br.gov.formosa.sigo2.model.enums.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {

    Page<Report> findByStatus(ReportStatus status, Pageable pageable);

    Page<Report> findByAssignedToAndStatus(User assignedTo, ReportStatus status, Pageable pageable);

    Page<Report> findByReporter(User reporter, Pageable pageable);

    Optional<Report> findByIdAndReporter(UUID id, User reporter);
}