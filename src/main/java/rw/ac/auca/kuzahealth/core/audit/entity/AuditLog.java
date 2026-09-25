package rw.ac.auca.kuzahealth.core.audit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rw.ac.auca.kuzahealth.utils.BaseEntity;

/**
 * Generic audit log that records who did what and when.
 * This is intended for UI consumption so admins can see changes.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "audit_logs")
public class AuditLog extends BaseEntity {

    // e.g., POST, PUT, PATCH, DELETE
    @Column(nullable = false, length = 10)
    private String action;

    // Full request URI like /api/v1/users/123
    @Column(nullable = false, length = 512)
    private String resource;

    // Optional entity identifier if present in path or request
    @Column(nullable = true, length = 128)
    private String entityId;

    @Column(nullable = true, length = 128)
    private String username;

    @Column(nullable = true, length = 256)
    private String email;

    @Column(nullable = true, length = 64)
    private String ip;

    public AuditLog(String action, String resource, String entityId, String username, String email, String ip) {
        this.action = action;
        this.resource = resource;
        this.entityId = entityId;
        this.username = username;
        this.email = email;
        this.ip = ip;
    }
}
