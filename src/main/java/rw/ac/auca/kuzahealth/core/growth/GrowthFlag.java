package rw.ac.auca.kuzahealth.core.growth;

/**
 * Screening flags. They point a health worker at a child to look at; they are not a diagnosis.
 */
public enum GrowthFlag {
    /** MUAC below 11.5 cm (6-59 months), or weight-for-length below -3 SD. */
    SEVERE_ACUTE_MALNUTRITION,
    /** MUAC from 11.5 to under 12.5 cm (6-59 months), or weight-for-length from -3 to under -2 SD. */
    MODERATE_ACUTE_MALNUTRITION,
    /** Weight-for-age below -3 SD. */
    SEVERELY_UNDERWEIGHT,
    /** Weight-for-age below -2 SD. */
    UNDERWEIGHT,
    /** Length-for-age below -3 SD. */
    SEVERELY_STUNTED,
    /** Length-for-age below -2 SD. */
    STUNTED,
    /** Weight-for-length above +2 SD. */
    POSSIBLE_OVERWEIGHT,
    /** Lighter than at the previous measurement. */
    WEIGHT_LOSS
}
