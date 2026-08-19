package com.travelapp.persistence.repositories;

import com.travelapp.persistence.mappers.WishlistMapper;
import com.travelapp.wishlist.domain.WishlistItem;
import com.travelapp.wishlist.ports.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
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
        return jpa.findByIdAndDeletedAtIsNull(id).map(mapper::toDomain);
    }

    @Override
    public List<WishlistItem> findByTripId(UUID tripId) {
        return jpa.findByTripIdAndDeletedAtIsNullOrderByPriorityAsc(tripId)
                  .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<WishlistItem> findByTripIdAndCity(UUID tripId, String city) {
        return jpa.findByTripIdAndDestinationCityAndDeletedAtIsNullOrderByPriorityAsc(tripId, city)
                  .stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public void deleteById(UUID id) { jpa.softDeleteById(id, OffsetDateTime.now()); }
}
