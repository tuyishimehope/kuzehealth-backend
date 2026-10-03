package rw.ac.auca.kuzahealth.core.anc;

public enum AncContactStatus {
    /** A completed antenatal visit took place around this contact. */
    ATTENDED,
    /** An antenatal visit is booked for this contact. */
    SCHEDULED,
    /** The contact date is still ahead and nothing is booked. */
    UPCOMING,
    /** The contact date has arrived and nothing is booked. */
    DUE,
    /** The contact window has passed without an attended visit. */
    MISSED
}
