package org.raflab.avanturaservice.repositoryLayer;

import org.raflab.avanturaservice.entityLayer.AdventureCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdventureCategoryRepository extends JpaRepository<AdventureCategory, Long> {
    boolean existsByName(String name);
}
