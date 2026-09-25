package rw.ac.auca.kuzahealth.controller.healthworker;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import rw.ac.auca.kuzahealth.core.healthworker.entity.HealthWorker;
import rw.ac.auca.kuzahealth.core.healthworker.service.HealthWorkerService;
import rw.ac.auca.kuzahealth.core.user.entity.User;
import rw.ac.auca.kuzahealth.core.user.repository.UserRepository;

@RestController
@RequestMapping("/api/health-workers")
public class HealthWorkerController {
    private static final Logger logger = LoggerFactory.getLogger(HealthWorkerController.class);

    private final HealthWorkerService healthWorkerService;
    private final UserRepository userRepository;

    @Autowired
    public HealthWorkerController(HealthWorkerService healthWorkerService, UserRepository userRepository) {
        this.healthWorkerService = healthWorkerService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> createHealthWorker(@RequestBody HealthWorker healthWorker) {
        try {
            // Find the User entity with the same email as the HealthWorker
            Optional<User> userOptional = userRepository.findByEmail(healthWorker.getEmail());

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // Set the reference to the User entity
                healthWorker.setUser(user);

                logger.info("Linking HealthWorker to User with email: {}", user.getEmail());
            } else {
                logger.warn("No User found with email: {}. HealthWorker will be created without a link to a User.", healthWorker.getEmail());
            }

            HealthWorker createdHealthWorker = healthWorkerService.createHealthWorker(healthWorker);
            return new ResponseEntity<>(createdHealthWorker, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating HealthWorker: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getHealthWorkers() {
        return ResponseEntity.ok(healthWorkerService.getAllHealthWorkers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateHealthWorker(@PathVariable("id") UUID id, @RequestBody HealthWorker healthWorker) {
        try {
            // Optionally re-link user based on email if provided
            if (healthWorker.getEmail() != null) {
                Optional<User> userOptional = userRepository.findByEmail(healthWorker.getEmail());
                userOptional.ifPresent(healthWorker::setUser);
            }
            HealthWorker updated = healthWorkerService.updateHealthWorker(id, healthWorker);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            logger.error("Error updating HealthWorker {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHealthWorker(@PathVariable("id") UUID id) {
        try {
            healthWorkerService.deleteHealthWorker(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting HealthWorker {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getHealthWorkerById(@PathVariable("id") UUID id){
        try {
            HealthWorker healthWorker = healthWorkerService.getHealthWorkerById(id);
            if (healthWorker != null) {
                return ResponseEntity.ok(healthWorker);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Health Worker not found.");
            }
        } catch (Exception e) {
            logger.error("Error fetching Health Worker {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
