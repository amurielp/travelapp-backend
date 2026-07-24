package com.travelapp.persistence.repositories;

import com.travelapp.gaps.domain.*;
import com.travelapp.gaps.ports.TripGapRepository;
import com.travelapp.persistence.mappers.TripGapMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class TripGapRepositoryAdapter implements TripGapRepository {

    private final TripGapJpaRepository jpa;
    private final TripGapMapper        mapper;

    @Override
    public TripGap save(TripGap gap) {
        return mapper.toDomain(jpa.save(mapper.toEntity(gap)));
    }

    @Override
    public List<TripGap> findOpenByTripId(UUID tripId) {
        return jpa.findByTripIdAndStatusOrderByAffectedFromAsc(tripId, GapStatus.OPEN.name())
                  .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<TripGap> findAllByTripId(UUID tripId) {
        return jpa.findByTripIdOrderByAffectedFromAsc(tripId)
                  .stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<TripGap> findById(UUID id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public void deleteByTripId(UUID tripId) { jpa.deleteByTripId(tripId); }

    @Override
    public void saveAll(List<TripGap> gaps) {
        jpa.saveAll(gaps.stream().map(mapper::toEntity).toList());
    }
}
