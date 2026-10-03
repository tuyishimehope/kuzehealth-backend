package rw.ac.auca.kuzahealth.core.anc;

/**
 * Reasons a pregnancy may need closer follow-up. Derived from what was recorded,
 * partly by keyword, so they prompt a clinical review rather than replace one.
 */
public enum RiskFlag {
    MARKED_HIGH_RISK,
    GRAND_MULTIPARITY,
    PREVIOUS_COMPLICATIONS,
    HYPERTENSIVE_DISORDER,
    DIABETES,
    HIV,
    ANAEMIA,
    PREVIOUS_CAESAREAN,
    MULTIPLE_PREGNANCY,
    BLEEDING,
    POST_TERM
}
