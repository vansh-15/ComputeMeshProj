package com.computemesh.worker_agent.client;

import com.computemesh.worker_agent.model.JobClaimResponse;
import com.computemesh.worker_agent.model.JobResultRequest;
import com.computemesh.worker_agent.model.WorkerRegistrationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class CoordinatorClient {

    private final RestClient restClient;

    public CoordinatorClient(
            RestClient.Builder builder,
            @Value("${computemesh.coordinator.base-url}") String baseUrl
    ) {
        this.restClient = builder
                .baseUrl(baseUrl)
                .build();
    }

    public WorkerRegistrationResponse registerWorker() {
        return restClient.post()
                .uri("/api/workers/register")
                .retrieve()
                .body(WorkerRegistrationResponse.class);
    }

    public JobClaimResponse claimJob(UUID workerId) {
        return restClient.post()
                .uri("/api/workers/{workerId}/jobs/claim", workerId)
                .retrieve()
                .body(JobClaimResponse.class);
    }



    public void submitResult(
            UUID jobId,
            UUID workerId,
            int attemptNumber,
            long count) {

        String resultData = "{\"count\":" + count + "}";

        restClient.post()
                .uri("/api/jobs/{jobId}/result", jobId)
                .body(new JobResultRequest(
                        workerId,
                        attemptNumber,
                        resultData))
                .retrieve()
                .toBodilessEntity();
    }

}
