package com.travelapp.gaps.ports;

import com.travelapp.gaps.domain.*;
import java.util.*;

public interface TripGapRepository {
    TripGap save(TripGap gap);
    List<TripGap> findOpenByTripId(UUID tripId);
    List<TripGap> findAllByTripId(UUID tripId);
    Optional<TripGap> findById(UUID id);
    void deleteByTripId(UUID tripId);    // para recalcular desde cero
    void saveAll(List<TripGap> gaps);
}
