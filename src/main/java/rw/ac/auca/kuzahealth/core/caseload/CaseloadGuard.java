package rw.ac.auca.kuzahealth.core.caseload;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import rw.ac.auca.kuzahealth.core.healthworker.entity.HealthWorker;
import rw.ac.auca.kuzahealth.core.healthworker.repository.HealthWorkerRepository;
import rw.ac.auca.kuzahealth.core.parent.entity.Parent;
import rw.ac.auca.kuzahealth.core.user.enums.EUserType;
import rw.ac.auca.kuzahealth.security.CustomUserDetails;

/**
 * Limits health workers to the parents assigned to them when
 * {@code app.caseload.enforce} is on. Administrators and data analysts are never
 * limited, and with enforcement off nobody is.
 */
@Component
@RequiredArgsConstructor
public class CaseloadGuard {

    /** Matches no health worker: used for a health worker account without a staff record. */
    private static final UUID NOBODY = new UUID(0, 0);

    private final HealthWorkerRepository healthWorkerRepository;

    @Value("${app.caseload.enforce:false}")
    private boolean enforce;

    /** The staff record id of the signed-in health worker, whether or not enforcement is on. */
    public Optional<UUID> currentHealthWorkerId() {
        return caller()
                .filter(user -> user.hasRole(EUserType.HEALTH_WORKER))
                .flatMap(user -> healthWorkerRepository.findByUser_Id(user.getId())
                        .or(() -> healthWorkerRepository.findByEmail(user.getEmail())))
                .map(HealthWorker::getId);
    }

    /**
     * The health worker the current request must be limited to.
     *
     * @return empty when the caller may see every parent
     */
    public Optional<UUID> restrictedTo() {
        if (!enforce) {
            return Optional.empty();
        }
        return caller()
                .filter(user -> user.hasRole(EUserType.HEALTH_WORKER))
                .map(user -> currentHealthWorkerId().orElse(NOBODY));
    }

    /** @throws AccessDeniedException when the parent is outside the caller's caseload */
    public void check(Parent parent) {
        restrictedTo().ifPresent(workerId -> {
            if (parent == null || !workerId.equals(parent.getAssignedHealthWorkerId())) {
                throw new AccessDeniedException("This record is not in your caseload");
            }
        });
    }

    /** Keeps only the items whose parent is in the caller's caseload. */
    public <T> List<T> filter(List<T> items, Function<T, Parent> parentOf) {
        Optional<UUID> workerId = restrictedTo();
        if (workerId.isEmpty()) {
            return items;
        }
        return items.stream()
                .filter(item -> {
                    Parent parent = parentOf.apply(item);
                    return parent != null && workerId.get().equals(parent.getAssignedHealthWorkerId());
                })
                .toList();
    }

    private static Optional<CustomUserDetails> caller() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getPrincipal() instanceof CustomUserDetails user
                ? Optional.of(user)
                : Optional.empty();
    }
}
