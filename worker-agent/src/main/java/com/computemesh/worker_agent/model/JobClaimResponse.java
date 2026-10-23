package com.computemesh.worker_agent.model;

import java.util.UUID;

public record JobClaimResponse(
        UUID id,
        String type,
        String status,
        String inputData
) {
}