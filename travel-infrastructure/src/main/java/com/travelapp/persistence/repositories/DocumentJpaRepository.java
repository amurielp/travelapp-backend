package com.travelapp.persistence.repositories;

import com.travelapp.persistence.entities.TravelDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface DocumentJpaRepository extends JpaRepository<TravelDocumentEntity, UUID> {
    List<TravelDocumentEntity> findByTripIdOrderByUploadedAtDesc(UUID tripId);
    List<TravelDocumentEntity> findByTripIdAndDocumentTypeIdOrderByUploadedAtDesc(UUID tripId, String documentTypeId);
}
