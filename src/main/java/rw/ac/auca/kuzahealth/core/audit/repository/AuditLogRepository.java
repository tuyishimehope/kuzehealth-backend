package rw.ac.auca.kuzahealth.core.audit.repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rw.ac.auca.kuzahealth.core.audit.entity.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    List<AuditLog> findByEmailOrderByCreatedAtDesc(String email);

    @Query("SELECT a FROM AuditLog a WHERE "
         + "(a.email = COALESCE(:email, a.email))"
         + " AND (a.action = COALESCE(:action, a.action))"
         + " AND (a.createdAt >= COALESCE(:fromDate, a.createdAt))"
         + " AND (a.createdAt <= COALESCE(:toDate, a.createdAt))"
         + " ORDER BY a.createdAt DESC")
    List<AuditLog> search(
        @Param("email") String email,
        @Param("action") String action,
        @Param("fromDate") Date fromDate,
        @Param("toDate") Date toDate
    );
}
