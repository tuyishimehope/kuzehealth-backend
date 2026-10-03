package rw.ac.auca.kuzahealth.core.immunisation;

public enum DoseStatus {
    /** A matching vaccination has been recorded. */
    GIVEN,
    /** Not yet due. */
    UPCOMING,
    /** Due now: the due date has arrived and the grace period has not run out. */
    DUE,
    /** Not given and past the grace period. */
    OVERDUE
}
