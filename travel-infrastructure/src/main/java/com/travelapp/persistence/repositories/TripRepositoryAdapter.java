package com.travelapp.persistence.repositories;

import com.travelapp.persistence.entities.TripStatusEntity;
import com.travelapp.trips.domain.Trip;
import com.travelapp.trips.ports.TripRepository;
import com.travelapp.persistence.mappers.TripMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class TripRepositoryAdapter implements TripRepository {

    private final TripJpaRepository jpa;
    private final TripMapper        mapper;

    @Override
    public Trip save(Trip trip) {
        return mapper.toDomain(jpa.save(mapper.toEntity(trip)));
    }

    @Override
    public Optional<Trip> findById(UUID id) {
        return jpa.findByIdAndDeletedAtIsNull(id).map(mapper::toDomain);
    }

    @Override
    public List<Trip> findByOwnerId(UUID ownerId) {
        return jpa.findByOwnerIdOrderByStartDateDesc(ownerId)
                  .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Trip> findSharedWith(UUID userId) {
        return jpa.findSharedWithUser(userId)
                  .stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public void deleteById(UUID id) { jpa.softDeleteById(id, OffsetDateTime.now()); }

    @Override
    public boolean existsByPublicSlug(String slug) {
        return jpa.existsByPublicSlug(slug);
    }

    @Override
    public List<Trip> findAllActive() {
        return jpa.findByStatusIn(List.of(TripStatusEntity.PLANNING, TripStatusEntity.ACTIVE))
                  .stream().map(mapper::toDomain).toList();
    }
}
