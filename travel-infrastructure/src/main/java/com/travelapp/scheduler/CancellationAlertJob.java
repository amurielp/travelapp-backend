package com.travelapp.scheduler;

import com.travelapp.notifications.usecases.SendCancellationDeadlineAlertUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

/** Envía alertas de deadline de cancelación — cada mañana a las 08:00 */
@Slf4j
@Component
@RequiredArgsConstructor
@DisallowConcurrentExecution
public class CancellationAlertJob implements Job {

    private final SendCancellationDeadlineAlertUseCase alertUseCase;

    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        log.info("CancellationAlertJob.start");
        alertUseCase.execute();
        log.info("CancellationAlertJob.done");
    }
}
