package com.travelapp.gaps.usecases;

import com.travelapp.gaps.domain.*;
import com.travelapp.gaps.ports.TripGapRepository;
import com.travelapp.shared.exceptions.TripNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class ResolveGapUseCase {

    private final TripGapRepository gapRepo;
    private final DetectTripGapsUseCase detector;

    @Transactional
    public TripGap ignore(UUID gapId, String reason) {
        var gap = gapRepo.findById(gapId)
            .orElseThrow(() -> new RuntimeException("Gap not found: " + gapId));
        gap.ignore(reason);
        return gapRepo.save(gap);
    }

    @Transactional
    public TripGap snooze(UUID gapId, OffsetDateTime until) {
        var gap = gapRepo.findById(gapId)
            .orElseThrow(() -> new RuntimeException("Gap not found: " + gapId));
        gap.snooze(until);
        return gapRepo.save(gap);
    }

    /** Se llama automáticamente tras crear/modificar un evento */
    @Transactional
    public void recalculate(UUID tripId) {
        detector.execute(tripId);
    }
}
