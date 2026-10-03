package rw.ac.auca.kuzahealth.core.immunisation;

/**
 * Coverage of one scheduled dose.
 *
 * @param eligible        infants old enough for the dose to be due
 * @param vaccinated      eligible infants with the dose recorded
 * @param coveragePercent vaccinated as a percentage of eligible; null when nobody is eligible
 */
public record CoverageRow(String code, String vaccineName, int doseNumber, long eligible, long vaccinated,
        Double coveragePercent) {
}
