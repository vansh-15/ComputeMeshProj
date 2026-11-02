package com.computemesh.coordinator.scheduler;

import com.computemesh.coordinator.service.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JobRecoveryScheduler {
    private static final Logger log =
            LoggerFactory.getLogger(JobRecoveryScheduler.class);

    private final JobService jobService;

    public JobRecoveryScheduler(JobService jobService) {
        this.jobService = jobService;
    }

    @Scheduled(fixedDelay = 10000)
    public void recoverExpiredJobs() {

        int recoveredJobs = jobService.recoverExpiredJobs();

        if (recoveredJobs > 0) {
            log.info("Recovered {} expired jobs", recoveredJobs);
        }
    }
}
