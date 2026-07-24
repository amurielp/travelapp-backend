package com.travelapp.persistence.repositories;

import com.travelapp.persistence.entities.WishlistItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface WishlistJpaRepository extends JpaRepository<WishlistItemEntity, UUID> {
    List<WishlistItemEntity> findByTripIdOrderByPriorityAsc(UUID tripId);
    List<WishlistItemEntity> findByTripIdAndDestinationCityOrderByPriorityAsc(UUID tripId, String destinationCity);
}
