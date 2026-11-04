
package com.computemesh.worker_agent.service;

import com.computemesh.worker_agent.client.CoordinatorClient;
import com.computemesh.worker_agent.model.JobClaimResponse;
import com.computemesh.worker_agent.model.JobResultRequest;
import com.computemesh.worker_agent.model.PrimeCountInput;
import com.computemesh.worker_agent.model.WorkerRegistrationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
public class WorkerStartupRunner implements CommandLineRunner {

    private static final Logger log =
            LoggerFactory.getLogger(WorkerStartupRunner.class);

    private final WorkerRegistrationService registrationService;
    private final CoordinatorClient coordinatorClient;
    private final PrimeCountService primeCountService;
    private final ObjectMapper objectMapper;

    private final ScheduledExecutorService heartbeatExecutor =
            Executors.newSingleThreadScheduledExecutor();

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

        heartbeatExecutor.scheduleAtFixedRate(() -> {
            try {
                coordinatorClient.sendWorkerHeartbeat(worker.id());

                log.info("Worker heartbeat sent: {}", worker.id());

            } catch (Exception e) {
                log.error("Failed to send worker heartbeat", e);
            }
        }, 0, 15, TimeUnit.SECONDS);

        while (!Thread.currentThread().isInterrupted()) {
            try {
                JobClaimResponse job =
                        coordinatorClient.claimJob(worker.id());

                log.info("Claim response received. Job is: {}", job);

                if (job == null) {
                    log.debug("No jobs available. Waiting...");
                    Thread.sleep(2000);
                    continue;
                }

                log.info("Claimed job: {}, type: {}",
                        job.id(), job.type());

                if ("PRIME_COUNT".equals(job.type())) {

                    ScheduledFuture<?> heartbeatTask =
                            heartbeatExecutor.scheduleAtFixedRate(() -> {
                                try {
                                    coordinatorClient.sendHeartbeat(
                                            worker.id(),
                                            job.id(),
                                            job.attemptNumber()
                                    );

                                    log.info("Heartbeat sent for job {}", job.id());

                                } catch (Exception e) {
                                    log.error("Heartbeat failed for job {}", job.id(), e);
                                }
                            }, 0, 15, TimeUnit.SECONDS);

                    try {
                        PrimeCountInput input = objectMapper.readValue(
                                job.inputData(), PrimeCountInput.class);

                        long result = primeCountService.countPrimes(input.limit());

                        log.info("PRIME_COUNT result: {}", result);

                        coordinatorClient.submitResult(
                                job.id(),
                                worker.id(),
                                job.attemptNumber(),
                                result
                        );

                        log.info("Job {} completed successfully", job.id());

                    } finally {
                        heartbeatTask.cancel(false);
                    }

                } else {
                    log.warn("Unsupported job type: {}", job.type());
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("Error while processing a job", e);

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        log.info("Worker polling loop stopped.");
    }
}
