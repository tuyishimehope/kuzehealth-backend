package rw.ac.auca.kuzahealth.core.immunisation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VaccineScheduleItemRepository extends JpaRepository<VaccineScheduleItem, UUID> {
    List<VaccineScheduleItem> findByActiveTrueOrderByDueAgeDaysAscCodeAsc();

    Optional<VaccineScheduleItem> findByCode(String code);

    boolean existsByCode(String code);
}
