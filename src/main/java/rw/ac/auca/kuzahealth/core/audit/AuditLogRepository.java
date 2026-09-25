package rw.ac.auca.kuzahealth.core.audit;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import rw.ac.auca.kuzahealth.core.audit.entity.AuditLog;

@NoRepositoryBean
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    List<AuditLog> findByEmailOrderByCreatedAtDesc(String email);

    @Query("SELECT a FROM AuditLog a WHERE (:email IS NULL OR a.email = :email)"
         + " AND (:action IS NULL OR a.action = :action)"
         + " AND (:fromDate IS NULL OR a.createdAt >= :fromDate)"
         + " AND (:toDate IS NULL OR a.createdAt <= :toDate)"
         + " ORDER BY a.createdAt DESC")
    List<AuditLog> search(
        @Param("email") String email,
        @Param("action") String action,
        @Param("fromDate") Date fromDate,
        @Param("toDate") Date toDate
    );
}
