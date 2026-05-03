package org.example.rezervacijeservice.repository;

import org.example.rezervacijeservice.model.AddOnItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddOnItemsRepository extends JpaRepository<AddOnItem, Long> {
}
