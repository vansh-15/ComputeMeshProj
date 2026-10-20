package com.computemesh.coordinator.dto;

import com.computemesh.coordinator.entity.JobStatus;

import java.time.Instant;
import java.util.UUID;

public record JobResponse(
        UUID id,
        String type,
        JobStatus status,
        Instant createAt
) {
}
