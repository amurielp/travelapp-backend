package com.travelapp.notifications.config;

import com.travelapp.notifications.scheduler.*;
import org.quartz.*;
import org.springframework.context.annotation.*;

@Configuration
public class NotificationsQuartzConfig {

    @Bean
    public JobDetail cancellationAlertJobDetail() {
        return JobBuilder.newJob(CancellationAlertJob.class)
            .withIdentity("cancellationAlertJob").storeDurably().build();
    }

    @Bean
    public Trigger cancellationAlertTrigger() {
        return TriggerBuilder.newTrigger()
            .forJob("cancellationAlertJob")
            .withIdentity("cancellationAlertTrigger")
            .withSchedule(CronScheduleBuilder.cronSchedule("0 0 8 * * ?"))
            .build();
    }

    @Bean
    public JobDetail gapDetectionJobDetail() {
        return JobBuilder.newJob(GapDetectionJob.class)
            .withIdentity("gapDetectionJob").storeDurably().build();
    }

    @Bean
    public Trigger gapDetectionTrigger() {
        return TriggerBuilder.newTrigger()
            .forJob("gapDetectionJob")
            .withIdentity("gapDetectionTrigger")
            .withSchedule(CronScheduleBuilder.cronSchedule("0 0 2 * * ?"))
            .build();
    }
}
