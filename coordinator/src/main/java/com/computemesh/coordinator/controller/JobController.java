package com.computemesh.coordinator.controller;

import com.computemesh.coordinator.dto.JobResponse;
import com.computemesh.coordinator.entity.Job;
import com.computemesh.coordinator.service.JobService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService){
        this.jobService=jobService;
    }

    @PostMapping
    public JobResponse createJob(@Valid @RequestBody CreateJobRequest jobRequest){
        Job job = jobService.createJob(jobRequest.type());
        return new JobResponse(
            job.getId(),
                job.getType(),
                job.getStatus(),
                job.getCreatedAt()
        );
    }
    public record CreateJobRequest(
            @NotBlank(message = "Job type is required")
            @Pattern(
                    regexp = "^(PRIME_COUNT|MATRIX_MULTIPLY|DATASET_STATS)$",
                    message = "Unsupported job type"
            )
            String type){

    }

    // Get Job By ID
    @GetMapping("/{id}")
    public JobResponse getJobById(@PathVariable UUID id){
        Job job = jobService.getJobById(id);
        return new JobResponse(
                job.getId(),
                job.getType(),
                job.getStatus(),
                job.getCreatedAt()
        );
    }

    @GetMapping
    public Page<JobResponse> getJobs(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size){
        return jobService.getJobs(page,size);
    }

}
