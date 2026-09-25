package rw.ac.auca.kuzahealth.core.healthworker.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import rw.ac.auca.kuzahealth.core.healthworker.entity.HealthWorker;

public interface HealthWorkerRepository extends JpaRepository<HealthWorker, UUID>{
    boolean existsByEmail(String email);
}
