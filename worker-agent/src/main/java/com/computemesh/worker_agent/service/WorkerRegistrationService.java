package com.computemesh.worker_agent.service;

import com.computemesh.worker_agent.client.CoordinatorClient;
import com.computemesh.worker_agent.model.WorkerRegistrationResponse;
import org.springframework.stereotype.Service;

@Service
public class WorkerRegistrationService {

    private final CoordinatorClient coordinatorClient;

    public WorkerRegistrationService(CoordinatorClient coordinatorClient){
        this.coordinatorClient=coordinatorClient;
    }

    public WorkerRegistrationResponse register(){
        return coordinatorClient.registerWorker();
    }

}
