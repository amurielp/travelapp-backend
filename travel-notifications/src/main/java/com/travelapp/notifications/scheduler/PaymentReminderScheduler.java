package com.travelapp.notifications.scheduler;

import com.travelapp.budget.usecases.SendPaymentRemindersUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentReminderScheduler {

    private final SendPaymentRemindersUseCase sendReminders;

    // Every 30 minutes
    @Scheduled(fixedDelay = 1_800_000)
    public void run() {
        log.info("PaymentReminderScheduler.start");
        int sent = sendReminders.execute();
        if (sent > 0) log.info("PaymentReminderScheduler.done — {} reminders sent", sent);
        else          log.debug("PaymentReminderScheduler.done — no reminders due");
    }
}
