package br.gov.formosa.sigo2.repository;

import br.gov.formosa.sigo2.model.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, String> {
    Optional<Configuration> findByKey(String key);
}
