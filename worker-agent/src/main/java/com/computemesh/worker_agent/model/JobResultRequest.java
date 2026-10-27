package com.computemesh.worker_agent.model;

import java.util.UUID;

public record JobResultRequest(
        UUID workerId,int attemptNumber, String resultData
) {
}
