package br.gov.formosa.sigo2.repository;

import br.gov.formosa.sigo2.model.ChecklistTemplateItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public interface ChecklistTemplateItemRepository extends JpaRepository<ChecklistTemplateItem, UUID> {

    default Map<UUID, ChecklistTemplateItem> findMapByIds(Set<UUID> ids) {
        return findAllById(ids).stream()
                .collect(Collectors.toMap(ChecklistTemplateItem::getId, item -> item));
    }

    @Modifying
    @Query("DELETE FROM ChecklistTemplateItem c WHERE c.id IN :ids")
    void deleteByIds(Collection<UUID> ids);
}