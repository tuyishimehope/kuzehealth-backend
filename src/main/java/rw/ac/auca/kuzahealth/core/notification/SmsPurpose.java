package rw.ac.auca.kuzahealth.core.notification;

public enum SmsPurpose {
    /** No longer sent (login codes were removed); kept so older log rows still load. */
    @Deprecated
    OTP,
    VISIT_SCHEDULED,
    VISIT_REMINDER,
    VISIT_MISSED,
    VACCINATION_RECORDED,
    VACCINATION_DUE,
    NUTRITION,
    MANUAL
}
