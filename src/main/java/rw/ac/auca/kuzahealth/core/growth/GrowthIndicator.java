package rw.ac.auca.kuzahealth.core.growth;

/**
 * WHO Child Growth Standards indicators. The file stem is the name of the reference
 * table, e.g. {@code wfa_boys.csv} and {@code wfa_girls.csv}.
 */
public enum GrowthIndicator {
    WEIGHT_FOR_AGE("wfa", true),
    LENGTH_FOR_AGE("lhfa", false),
    WEIGHT_FOR_LENGTH("wfl", true),
    HEAD_CIRCUMFERENCE_FOR_AGE("hcfa", false);

    private final String fileStem;
    private final boolean weightBased;

    GrowthIndicator(String fileStem, boolean weightBased) {
        this.fileStem = fileStem;
        this.weightBased = weightBased;
    }

    public String getFileStem() {
        return fileStem;
    }

    /** Weight-based indicators use WHO's adjusted formula beyond 3 standard deviations. */
    public boolean isWeightBased() {
        return weightBased;
    }
}
