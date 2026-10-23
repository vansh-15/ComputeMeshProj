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

        return Optional.of(jobRepository.save(job));
    }

    @Transactional
    public Job submitResult(UUID jobId, String resultData) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Job not found: " + jobId
                ));

        if (job.getStatus() != JobStatus.RUNNING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only RUNNING jobs can receive results"
            );
        }

        job.setResultData(resultData);
        job.setStatus(JobStatus.SUCCEEDED);

        return jobRepository.save(job);
    }

}
