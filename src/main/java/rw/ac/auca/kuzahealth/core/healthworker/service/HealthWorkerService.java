package rw.ac.auca.kuzahealth.core.healthworker.service;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rw.ac.auca.kuzahealth.core.healthworker.entity.HealthWorker;
import rw.ac.auca.kuzahealth.core.healthworker.repository.HealthWorkerRepository;

@Service
public class HealthWorkerService {
    private static final Logger logger = LoggerFactory.getLogger(HealthWorkerService.class);

    private final HealthWorkerRepository healthWorkerRepository;

    @Autowired
    public HealthWorkerService(HealthWorkerRepository healthWorkerRepository) {
        this.healthWorkerRepository = healthWorkerRepository;
    }

    public HealthWorker createHealthWorker(HealthWorker healthWorker) {
        logger.info("Creating HealthWorker with email: {}", healthWorker.getEmail());

        // Check if the email already exists
        boolean emailExists = healthWorkerRepository.existsByEmail(healthWorker.getEmail());
        logger.info("Email exists check: {}", emailExists);

        if (emailExists) {
            logger.error("HealthWorker with email {} already exists", healthWorker.getEmail());
            throw new IllegalArgumentException("Email already exists");
        }

        HealthWorker savedHealthWorker = healthWorkerRepository.save(healthWorker);
        logger.info("HealthWorker saved with ID: {}", savedHealthWorker.getId());

        return savedHealthWorker;
    }

    public List<HealthWorker> getAllHealthWorkers() {
        return healthWorkerRepository.findAll();
    }

    public HealthWorker updateHealthWorker(UUID id, HealthWorker updates) {
        logger.info("Updating HealthWorker with ID: {}", id);
        HealthWorker existing = healthWorkerRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("HealthWorker not found"));

        // Handle email change uniqueness
        if (updates.getEmail() != null && !updates.getEmail().equals(existing.getEmail())) {
            boolean emailExists = healthWorkerRepository.existsByEmail(updates.getEmail());
            if (emailExists) {
                logger.error("Cannot update HealthWorker {}: email {} already in use", id, updates.getEmail());
                throw new IllegalArgumentException("Email already exists");
            }
            existing.setEmail(updates.getEmail());
        }

        if (updates.getFirst_name() != null) existing.setFirst_name(updates.getFirst_name());
        if (updates.getLast_name() != null) existing.setLast_name(updates.getLast_name());
        if (updates.getPhone_number() != null) existing.setPhone_number(updates.getPhone_number());
        if (updates.getQualification() != null) existing.setQualification(updates.getQualification());
        if (updates.getService_area() != null) existing.setService_area(updates.getService_area());
        if (updates.getUser() != null) existing.setUser(updates.getUser());

        HealthWorker saved = healthWorkerRepository.save(existing);
        logger.info("HealthWorker updated with ID: {}", saved.getId());
        return saved;
    }

    public void deleteHealthWorker(UUID id) {
        logger.info("Deleting HealthWorker with ID: {}", id);
        if (!healthWorkerRepository.existsById(id)) {
            throw new IllegalArgumentException("HealthWorker not found");
        }
        healthWorkerRepository.deleteById(id);
    }

    public HealthWorker getHealthWorkerById(UUID id) {
        logger.info("Fetching HealthWorker with ID: {}", id);
        return healthWorkerRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("HealthWorker not found"));
    }
}
