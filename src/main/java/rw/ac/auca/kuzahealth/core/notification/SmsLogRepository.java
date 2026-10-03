package rw.ac.auca.kuzahealth.core.notification;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SmsLogRepository extends JpaRepository<SmsLog, UUID>, JpaSpecificationExecutor<SmsLog> {
}
