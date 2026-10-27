package com.computemesh.coordinator.repository;

import com.computemesh.coordinator.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {

    @Query(value = """
    SELECT *
    FROM job
    WHERE status = 'QUEUED'
    ORDER BY created_at ASC
    LIMIT 1
    FOR UPDATE SKIP LOCKED
    """, nativeQuery = true)
    List<Job> findNextQueuedJobForUpdate();

    @Modifying
    @Query("""
    UPDATE Job j
    SET j.status = com.computemesh.coordinator.entity.JobStatus.QUEUED,
        j.worker = null,
        j.leaseExpiresAt = null
    WHERE j.status = com.computemesh.coordinator.entity.JobStatus.RUNNING
      AND j.leaseExpiresAt <= :now
    """)
    int requeueExpiredJobs(@Param("now") Instant now);

}
