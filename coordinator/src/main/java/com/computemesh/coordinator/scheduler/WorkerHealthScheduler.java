package com.computemesh.coordinator.scheduler;

import com.computemesh.coordinator.service.WorkerService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WorkerHealthScheduler {

    private final WorkerService workerService;

    public WorkerHealthScheduler(WorkerService workerService) {
        this.workerService = workerService;
    }

    @Scheduled(fixedDelay = 10000)
    public void checkWorkerHealth() {
        workerService.markOfflineWorkers();
    }
}