package com.travelapp.notifications.scheduler;

import com.travelapp.gaps.usecases.DetectTripGapsUseCase;
import com.travelapp.trips.ports.TripRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

/** Recalcula huecos para todos los viajes activos — cada noche a las 02:00. */
@Slf4j
@Component
@RequiredArgsConstructor
@DisallowConcurrentExecution
public class GapDetectionJob implements Job {

    private final DetectTripGapsUseCase detector;
    private final TripRepository        trips;

    @Override
    public void execute(JobExecutionContext ctx) {
        log.info("GapDetectionJob.start");
        trips.findAllActive().forEach(trip -> {
            try {
                var gaps = detector.execute(trip.getId());
                log.debug("GapDetectionJob tripId={} gaps={}", trip.getId(), gaps.size());
            } catch (Exception e) {
                log.error("GapDetectionJob.error tripId={}", trip.getId(), e);
            }
        });
        log.info("GapDetectionJob.done");
    }
}
