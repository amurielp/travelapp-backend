package com.travelapp.persistence.repositories;

import com.travelapp.persistence.entities.SuggestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.*;

public interface SuggestionJpaRepository extends JpaRepository<SuggestionEntity, UUID> {
    List<SuggestionEntity> findByTripIdAndDate(UUID tripId, LocalDate date);
}
