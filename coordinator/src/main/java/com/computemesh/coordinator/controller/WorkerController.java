
package com.computemesh.coordinator.controller;

import com.computemesh.coordinator.dto.JobClaimResponse;
import com.computemesh.coordinator.entity.Job;
import com.computemesh.coordinator.entity.Worker;
import com.computemesh.coordinator.service.JobService;
import com.computemesh.coordinator.service.WorkerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/workers")
public class WorkerController {

    private final WorkerService workerService;
    private final JobService jobService;

    public WorkerController(
            WorkerService workerService,
            JobService jobService) {
        this.workerService = workerService;
        this.jobService = jobService;
    }

    @PostMapping("/register")
    public Worker registerWorker() {
        return workerService.registerWorker();
    }

    @PostMapping("/{workerId}/jobs/claim")
    public ResponseEntity<JobClaimResponse> claimJob(
            @PathVariable UUID workerId) {

        Worker worker = workerService.getWorkerById(workerId);

        return jobService.claimNextJob(worker)
                .map(job -> ResponseEntity.ok(toJobClaimResponse(job)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    private JobClaimResponse toJobClaimResponse(Job job) {
        return new JobClaimResponse(
                job.getId(),
                job.getType(),
                job.getStatus().name(),
                job.getInputData(),
                job.getAttemptNumber()
        );
    }

    @PostMapping("/{workerId}/jobs/{jobId}/heartbeat")
    public ResponseEntity<Void> heartbeat(
            @PathVariable UUID workerId,
            @PathVariable UUID jobId,
            @RequestParam int attemptNumber) {

        jobService.heartbeat(jobId, workerId, attemptNumber);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{workerId}/heartbeat")
    public ResponseEntity<Void> workerHeartbeat(
            @PathVariable UUID workerId) {

        workerService.heartbeat(workerId);

        return ResponseEntity.noContent().build();
    }

}
