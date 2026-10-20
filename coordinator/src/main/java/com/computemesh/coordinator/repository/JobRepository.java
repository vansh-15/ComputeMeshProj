package com.computemesh.coordinator.repository;

import com.computemesh.coordinator.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface JobRepository extends JpaRepository<Job, UUID> {

}
