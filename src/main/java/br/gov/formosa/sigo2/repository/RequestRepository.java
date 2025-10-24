package br.gov.formosa.sigo2.repository;

import br.gov.formosa.sigo2.model.Request;
import br.gov.formosa.sigo2.model.User;
import br.gov.formosa.sigo2.model.enums.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface RequestRepository extends JpaRepository<Request, UUID> {

    Page<Request> findByApplicant(User applicant, Pageable pageable);

    Page<Request> findByStatus(RequestStatus status, Pageable pageable);

    List<Request> findByStatusAndExpiresAt(RequestStatus status, LocalDate expiresAt);
}