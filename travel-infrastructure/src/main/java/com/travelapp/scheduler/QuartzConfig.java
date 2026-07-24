package com.travelapp.scheduler;

import org.quartz.*;
import org.springframework.context.annotation.*;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail gapDetectionJobDetail() {
        return JobBuilder.newJob(GapDetectionJob.class)
            .withIdentity("gapDetectionJob")
            .storeDurably()
            .build();
    }

    @Bean
    public Trigger gapDetectionTrigger(JobDetail gapDetectionJobDetail) {
        return TriggerBuilder.newTrigger()
            .forJob(gapDetectionJobDetail)
            .withIdentity("gapDetectionTrigger")
            .withSchedule(CronScheduleBuilder.cronSchedule("0 0 2 * * ?"))  // 02:00 cada noche
            .build();
    }

    @Bean
    public JobDetail cancellationAlertJobDetail() {
        return JobBuilder.newJob(CancellationAlertJob.class)
            .withIdentity("cancellationAlertJob")
            .storeDurably()
            .build();
    }

    @Bean
    public Trigger cancellationAlertTrigger(JobDetail cancellationAlertJobDetail) {
        return TriggerBuilder.newTrigger()
            .forJob(cancellationAlertJobDetail)
            .withIdentity("cancellationAlertTrigger")
            .withSchedule(CronScheduleBuilder.cronSchedule("0 0 8 * * ?"))  // 08:00 cada mañana
            .build();
    }
}
