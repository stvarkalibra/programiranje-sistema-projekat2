package org.raflab.avanturaservice.repositoryLayer;

import org.raflab.avanturaservice.entityLayer.GearRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GearRequirementRepository extends JpaRepository<GearRequirement, Long> {
    boolean existsByItemNameAndAdventureId(String itemName, Long adventureId);
}
