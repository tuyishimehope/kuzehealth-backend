package rw.ac.auca.kuzahealth.core.growth;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GrowthMeasurementRepository extends JpaRepository<GrowthMeasurement, UUID> {
    List<GrowthMeasurement> findByInfant_IdOrderByMeasuredAtAscCreatedAtAsc(UUID infantId);
}
