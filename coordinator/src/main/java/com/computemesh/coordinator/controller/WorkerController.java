package com.computemesh.coordinator.controller;

import com.computemesh.coordinator.dto.JobResponse;
import com.computemesh.coordinator.entity.Job;
import com.computemesh.coordinator.entity.Worker;
import com.computemesh.coordinator.service.JobService;
import com.computemesh.coordinator.service.WorkerService;
import org.hibernate.jdbc.Work;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/workers")
public class WorkerController {
    private final WorkerService workerService;

    private final JobService jobService;

    public WorkerController(WorkerService workerService, JobService jobService){
        this.workerService=workerService;
        this.jobService=jobService;
    }

    @PostMapping("/register")
    public Worker registerWorker(){
        return workerService.registerWorker();
    }

    @PostMapping("/{workerId}/jobs/claim")
    public ResponseEntity<JobResponse> claimJob(@PathVariable UUID workerId){
        Worker worker = workerService.getWorkerById(workerId);

        return jobService.claimNextJob(worker)
                .map(job -> ResponseEntity.ok(toJobResponse(job)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    private JobResponse toJobResponse(Job job){
        return new JobResponse(
                job.getId(),
                job.getType(),
                job.getStatus(),
                job.getCreatedAt()
        );
    }


}