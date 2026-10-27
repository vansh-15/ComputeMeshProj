
package com.computemesh.coordinator.controller;

import com.computemesh.coordinator.dto.JobResponse;
import com.computemesh.coordinator.dto.JobResultRequest;
import com.computemesh.coordinator.entity.Job;
import com.computemesh.coordinator.service.JobService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    public JobResponse createJob(
            @Valid @RequestBody CreateJobRequest jobRequest) {

        Job job = jobService.createJob(
                jobRequest.type(),
                jobRequest.inputData()
        );

        return toResponse(job);
    }

    public record CreateJobRequest(

            @NotBlank(message = "Job type is required")
            @Pattern(
                    regexp = "^(PRIME_COUNT|MATRIX_MULTIPLY|DATASET_STATS)$",
                    message = "Unsupported job type"
            )
            String type,

            @NotNull(message = "Input data is required")
            String inputData

    ) {
    }

    @GetMapping("/{id}")
    public JobResponse getJobById(@PathVariable UUID id) {
        Job job = jobService.getJobById(id);
        return toResponse(job);
    }

    @GetMapping
    public Page<JobResponse> getJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return jobService.getJobs(page, size);
    }

    private JobResponse toResponse(Job job) {
        return new JobResponse(
                job.getId(),
                job.getType(),
                job.getStatus(),
                job.getCreatedAt()
        );
    }



    @PostMapping("/{id}/result")
    public ResponseEntity<Void> submitResult(
            @PathVariable UUID id,
            @Valid @RequestBody JobResultRequest request) {

        jobService.submitResult(
                id,
                request.workerId(),
                request.attemptNumber(),
                request.resultData()
        );

        return ResponseEntity.noContent().build();
    }

}