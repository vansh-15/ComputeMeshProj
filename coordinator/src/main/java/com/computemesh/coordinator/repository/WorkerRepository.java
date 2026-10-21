package com.computemesh.coordinator.repository;

import com.computemesh.coordinator.entity.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WorkerRepository extends JpaRepository<Worker,UUID> {
}
