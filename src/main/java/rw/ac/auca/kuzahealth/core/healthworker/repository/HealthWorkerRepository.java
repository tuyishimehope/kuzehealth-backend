package rw.ac.auca.kuzahealth.core.healthworker.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import rw.ac.auca.kuzahealth.core.healthworker.entity.HealthWorker;

public interface HealthWorkerRepository
        extends JpaRepository<HealthWorker, UUID>, JpaSpecificationExecutor<HealthWorker> {
    boolean existsByEmail(String email);

    Optional<HealthWorker> findByUser_Id(UUID userId);

    Optional<HealthWorker> findByEmail(String email);
}
