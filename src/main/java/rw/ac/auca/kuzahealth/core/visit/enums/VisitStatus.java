package rw.ac.auca.kuzahealth.core.visit.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Lifecycle of a visit. Stored and serialised as the display label ("Scheduled"),
 * which is what the column already held when the status was free text.
 */
public enum VisitStatus {
    SCHEDULED("Scheduled"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    MISSED("Missed"),
    CANCELLED("Cancelled");

    private final String label;

    VisitStatus(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    /** Accepts the label or the constant name, ignoring case, spaces, dashes and underscores. */
    @JsonCreator
    public static VisitStatus parse(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String wanted = normalise(value);
        for (VisitStatus status : values()) {
            if (normalise(status.label).equals(wanted)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown visit status: " + value);
    }

    private static String normalise(String value) {
        return value.replaceAll("[\\s_-]", "").toLowerCase();
    }

    @Converter(autoApply = true)
    public static class JpaConverter implements AttributeConverter<VisitStatus, String> {
        @Override
        public String convertToDatabaseColumn(VisitStatus status) {
            return status == null ? null : status.label;
        }

        @Override
        public VisitStatus convertToEntityAttribute(String value) {
            try {
                return parse(value);
            } catch (IllegalArgumentException e) {
                // Free-text value written before the enum existed
                return SCHEDULED;
            }
        }
    }
}
