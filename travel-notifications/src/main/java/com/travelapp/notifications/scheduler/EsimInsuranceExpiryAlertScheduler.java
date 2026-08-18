package com.travelapp.notifications.scheduler;

import com.travelapp.notifications.usecases.SendEsimInsuranceExpiryAlertUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Envía alertas de vencimiento de eSIM y seguro de viaje — cada mañana a las 09:00. */
@Slf4j
@Component
@RequiredArgsConstructor
public class EsimInsuranceExpiryAlertScheduler {

    private final SendEsimInsuranceExpiryAlertUseCase alertUseCase;

    @Scheduled(cron = "0 0 9 * * *")
    public void run() {
        log.info("EsimInsuranceExpiryAlertScheduler.start");
        alertUseCase.execute();
        log.info("EsimInsuranceExpiryAlertScheduler.done");
    }
}
