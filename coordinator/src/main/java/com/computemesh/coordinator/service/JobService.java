package com.computemesh.coordinator.service;

import com.computemesh.coordinator.entity.Job;
import com.computemesh.coordinator.entity.JobStatus;
import com.computemesh.coordinator.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JobService {

    private final JobRepository jobRepository;


    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public Job createJob(String type){
        Job job = new Job();
        job.setType(type);
        job.setStatus(JobStatus.QUEUED);
        job.setCreatedAt(Instant.now());
        return jobRepository.save(job);
    }
}
