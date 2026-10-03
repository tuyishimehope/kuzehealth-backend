package rw.ac.auca.kuzahealth.core.notification;

public enum SmsStatus {
    /** Accepted by the SMS provider. This is not a delivery receipt from the handset. */
    SENT,
    /** Rejected by the provider or the provider could not be reached. */
    FAILED,
    /** Not sent because the parent has not agreed to receive SMS. */
    SKIPPED_NO_CONSENT,
    /** Not sent because there is no phone number on record. */
    SKIPPED_NO_PHONE;

    public boolean isSent() {
        return this == SENT;
    }
}
