package com.computemesh.coordinator;

import com.computemesh.coordinator.dto.JobResponse;
import com.computemesh.coordinator.entity.Job;
import com.computemesh.coordinator.entity.JobStatus;
import com.computemesh.coordinator.repository.JobRepository;
import com.computemesh.coordinator.service.JobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JobServiceTest {
    private JobRepository jobRepository;
    private JobService jobService;

    @BeforeEach
    void setUp() {
        jobRepository = mock(JobRepository.class);
        jobService = new JobService(jobRepository);
    }

    @Test
    void createJobShouldSaveQueuedJob() {
        when(jobRepository.save(
                org.mockito.ArgumentMatchers.any(Job.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Job job = jobService.createJob("PRIME_COUNT");

        assertEquals("PRIME_COUNT", job.getType());
        assertEquals(JobStatus.QUEUED, job.getStatus());
        assertNotNull(job.getCreatedAt());

        verify(jobRepository).save(job);
    }

    @Test
    void getJobByIdShouldThrowNotFoundWhenJobDoesNotExist() {
        UUID id = UUID.randomUUID();

        when(jobRepository.findById(id))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> jobService.getJobById(id)
        );

        assertEquals(404, exception.getStatusCode().value());
    }

    @Test
    void getJobsShouldReturnPageOfJobResponses() {
        Job job = new Job();
        job.setType("PRIME_COUNT");
        job.setStatus(JobStatus.QUEUED);
        job.setCreatedAt(java.time.Instant.now());

        Pageable pageable = PageRequest.of(0, 5);

        Page<Job> jobPage = new PageImpl<>(
                List.of(job),
                pageable,
                1
        );

        when(jobRepository.findAll(any(Pageable.class)))
                .thenReturn(jobPage);

        Page<JobResponse> result = jobService.getJobs(0, 5);

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("PRIME_COUNT", result.getContent().get(0).type());
        assertEquals(
                JobStatus.QUEUED,
                result.getContent().get(0).status()
        );

        verify(jobRepository).findAll(any(Pageable.class));
    }

}

