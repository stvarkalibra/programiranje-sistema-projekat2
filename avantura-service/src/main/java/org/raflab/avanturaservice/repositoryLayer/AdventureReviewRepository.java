package org.raflab.avanturaservice.repositoryLayer;

import org.raflab.avanturaservice.entityLayer.AdventureReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdventureReviewRepository extends JpaRepository<AdventureReview, Long> {
}
