package br.gov.formosa.sigo2.repository;

import br.gov.formosa.sigo2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByFullName(String fullName);
    Optional<User> findByEmail(String email);
    Optional<User> findByCpf(String cpf);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.role.id = :roleId")
    boolean existsByRoleId(@Param("roleId") UUID roleId);
}
