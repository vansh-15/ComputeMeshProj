package com.computemesh.coordinator.service;

import com.computemesh.coordinator.dto.JobResponse;
import com.computemesh.coordinator.entity.Job;
import com.computemesh.coordinator.entity.JobStatus;
import com.computemesh.coordinator.entity.Worker;
import com.computemesh.coordinator.repository.JobRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class JobService {

    private final JobRepository jobRepository;


    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public Job createJob(String type, String inputData) {
        Job job = new Job();

        job.setType(type);
        job.setInputData(inputData);
        job.setStatus(JobStatus.QUEUED);
        job.setCreatedAt(Instant.now());

        return jobRepository.save(job);
    }

    public Job getJobById(UUID id){
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Job not found"+id
                ));
    }


    // get all jobs using pagination concept (one page displays a fixed amount of jobs)
    public Page<JobResponse> getJobs(int page,int size){
        if(page<0){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page number cannot be negative"
            );
        }
        if(size<1 || size>100){
            throw new ResponseStatusException(
              HttpStatus.BAD_REQUEST,
              "Page size must be between 1 and 100"
            );
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC,"createdAt")
        );
        return jobRepository.findAll(pageable)
                .map(job->new JobResponse(
                        job.getId(),
                        job.getType(),
                        job.getStatus(),
                        job.getCreatedAt()
                ));
    }

    @Transactional
    public Optional<Job> claimNextJob(Worker worker){
        List<Job> jobs = jobRepository.findNextQueuedJobForUpdate();
        if(jobs.isEmpty()){
            return Optional.empty();
        }
        Job job = jobs.get(0);
        job.setStatus(JobStatus.RUNNING);
        job.setWorker(worker);
        job.setLeaseExpiresAt(Instant.now().plusSeconds(60));
        job.setAttemptNumber(job.getAttemptNumber() + 1);

        return Optional.of(jobRepository.save(job));
    }

    @Transactional
    public int recoverExpiredJobs() {
        return jobRepository.requeueExpiredJobs(Instant.now());
    }



    @Transactional
    public Job submitResult(
            UUID jobId,
            UUID workerId,
            int attemptNumber,
            String resultData) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Job not found: " + jobId));

        if (job.getStatus() != JobStatus.RUNNING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only RUNNING jobs can receive results");
        }

        if (job.getWorker() == null
                || !job.getWorker().getId().equals(workerId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Worker is not assigned to this job");
        }

        if (job.getAttemptNumber() != attemptNumber) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Stale attempt: submitted attempt does not match current attempt");
        }

        if (job.getLeaseExpiresAt() == null
                || !job.getLeaseExpiresAt().isAfter(Instant.now())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Job lease has expired");
        }

        job.setResultData(resultData);
        job.setStatus(JobStatus.SUCCEEDED);
        job.setLeaseExpiresAt(null);

        return jobRepository.save(job);
    }

}
