package com.computemesh.coordinator.service;

import com.computemesh.coordinator.entity.Worker;
import com.computemesh.coordinator.entity.WorkerStatus;
import com.computemesh.coordinator.repository.WorkerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
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

        return workerRepository.save(worker);
    }

    public Worker getWorkerById(UUID id){
        return workerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Worker not found: "+id
                ));
    }

}
