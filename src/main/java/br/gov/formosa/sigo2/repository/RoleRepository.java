package br.gov.formosa.sigo2.repository;

import br.gov.formosa.sigo2.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByNameIgnoreCase(String name);
    Optional<Role> findByName(String name);

}
