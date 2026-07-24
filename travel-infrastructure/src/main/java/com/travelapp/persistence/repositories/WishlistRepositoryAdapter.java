package com.travelapp.persistence.repositories;

import com.travelapp.persistence.mappers.WishlistMapper;
import com.travelapp.wishlist.domain.WishlistItem;
import com.travelapp.wishlist.ports.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class WishlistRepositoryAdapter implements WishlistRepository {

    private final WishlistJpaRepository jpa;
    private final WishlistMapper        mapper;

    @Override
    public WishlistItem save(WishlistItem item) {
        return mapper.toDomain(jpa.save(mapper.toEntity(item)));
    }

    @Override
    public Optional<WishlistItem> findById(UUID id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<WishlistItem> findByTripId(UUID tripId) {
        return jpa.findByTripIdOrderByPriorityAsc(tripId)
                  .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<WishlistItem> findByTripIdAndCity(UUID tripId, String city) {
        return jpa.findByTripIdAndDestinationCityOrderByPriorityAsc(tripId, city)
                  .stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) { jpa.deleteById(id); }
}
