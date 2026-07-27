package com.travelapp.notifications.scheduler;

import com.travelapp.notifications.usecases.SendCancellationDeadlineAlertUseCase;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Envía alertas de deadline de cancelación — cada mañana a las 08:00. */
@Slf4j
@Component
@DisallowConcurrentExecution
public class CancellationAlertJob implements Job {

    @Autowired
    private SendCancellationDeadlineAlertUseCase alertUseCase;

    @Override
    public void execute(JobExecutionContext ctx) {
        log.info("CancellationAlertJob.start");
        alertUseCase.execute();
        log.info("CancellationAlertJob.done");
    }
}
