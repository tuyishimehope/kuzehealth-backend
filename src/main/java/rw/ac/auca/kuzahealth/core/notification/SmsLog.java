package rw.ac.auca.kuzahealth.core.notification;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import rw.ac.auca.kuzahealth.utils.BaseEntity;

/**
 * Record of an SMS the system sent or decided not to send.
 */
@Entity
@Getter
@Setter
@Table(name = "sms_log")
public class SmsLog extends BaseEntity {

    @Column(length = 64)
    private String recipient;

    /** The text that was sent. Login codes are never stored here. */
    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(length = 32)
    private String sender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private SmsPurpose purpose;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private SmsStatus status;

    @Column(name = "provider_response", columnDefinition = "TEXT")
    private String providerResponse;

    @Column(name = "parent_id")
    private UUID parentId;

    /** Email of the user whose action caused the message, or "system" for scheduled jobs. */
    @Column(name = "triggered_by")
    private String triggeredBy;
}
