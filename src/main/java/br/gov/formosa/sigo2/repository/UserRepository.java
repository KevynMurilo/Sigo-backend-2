package br.gov.formosa.sigo2.repository;

import br.gov.formosa.sigo2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByFullName(String fullName);
    Optional<User> findByEmail(String email);
}
