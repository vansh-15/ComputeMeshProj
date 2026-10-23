
package com.computemesh.worker_agent.service;

import com.computemesh.worker_agent.client.CoordinatorClient;
import com.computemesh.worker_agent.model.JobClaimResponse;
import com.computemesh.worker_agent.model.PrimeCountInput;
import com.computemesh.worker_agent.model.WorkerRegistrationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class WorkerStartupRunner implements CommandLineRunner {

    private static final Logger log =
            LoggerFactory.getLogger(WorkerStartupRunner.class);

    private final WorkerRegistrationService registrationService;
    private final CoordinatorClient coordinatorClient;
    private final PrimeCountService primeCountService;
    private final ObjectMapper objectMapper;

    public WorkerStartupRunner(
            WorkerRegistrationService registrationService,
            CoordinatorClient coordinatorClient,
            PrimeCountService primeCountService,
            ObjectMapper objectMapper) {

        this.registrationService = registrationService;
        this.coordinatorClient = coordinatorClient;
        this.primeCountService = primeCountService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        WorkerRegistrationResponse worker =
                registrationService.register();

        log.info("Worker registered successfully. Worker ID: {}",
                worker.id());

        JobClaimResponse job =
                coordinatorClient.claimJob(worker.id());

        if (job == null) {
            log.info("No queued jobs available.");
            return;
        }

        log.info("Claimed job: {}, type: {}, status: {}",
                job.id(), job.type(), job.status());

        log.info("Received job input: {}", job.inputData());

        if ("PRIME_COUNT".equals(job.type())) {
            PrimeCountInput input = objectMapper.readValue(
                    job.inputData(),
                    PrimeCountInput.class
            );

            long result =
                    primeCountService.countPrimes(input.limit());

            log.info("PRIME_COUNT result for limit {}: {}",
                    input.limit(), result);


            coordinatorClient.submitResult(job.id(), result);

            log.info("Result submission completed for job {}", job.id());
        } else {
            log.warn("Unsupported job type for execution: {}",
                    job.type());
        }
    }
}
