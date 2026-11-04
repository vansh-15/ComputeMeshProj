package com.computemesh.coordinator.service;

import com.computemesh.coordinator.entity.Worker;
import com.computemesh.coordinator.entity.WorkerStatus;
import com.computemesh.coordinator.repository.WorkerRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class WorkerService {

    private final WorkerRepository workerRepository;

    public WorkerService(WorkerRepository workerRepository){
        this.workerRepository=workerRepository;
    }

    public Worker registerWorker(){
        Worker worker = new Worker();
        worker.setStatus(WorkerStatus.ACTIVE);
        worker.setRegisteredAt(Instant.now());

        worker.setLastHeartbeatAt(Instant.now());
        worker.setStatus(WorkerStatus.ACTIVE);

        return workerRepository.save(worker);
    }

    public Worker getWorkerById(UUID id){
        return workerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Worker not found: "+id
                ));
    }

    @Transactional
    public void heartbeat(UUID workerId) {

        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Worker not found"));

        worker.setLastHeartbeatAt(Instant.now());
        worker.setStatus(WorkerStatus.ACTIVE);

        workerRepository.save(worker);
    }

    @Transactional
    public void markOfflineWorkers() {

        Instant timeout = Instant.now().minusSeconds(45);

        List<Worker> workers = workerRepository.findAll();

        for (Worker worker : workers) {

            if (worker.getLastHeartbeatAt() == null ||
                    worker.getLastHeartbeatAt().isBefore(timeout)) {

                worker.setStatus(WorkerStatus.OFFLINE);
            }
        }

        workerRepository.saveAll(workers);
    }

}
