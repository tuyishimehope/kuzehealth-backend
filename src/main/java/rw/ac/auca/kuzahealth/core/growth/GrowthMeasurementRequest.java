package rw.ac.auca.kuzahealth.core.growth;

import java.util.Date;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * At least one of the four measurements must be present. The ranges only reject
 * values that cannot belong to a child under five, such as a weight typed in grams.
 */
@Data
public class GrowthMeasurementRequest {

    @NotNull
    @PastOrPresent
    private Date measuredAt;

    @DecimalMin("0.3")
    @DecimalMax("40")
    private Double weightKg;

    @DecimalMin("20")
    @DecimalMax("130")
    private Double heightCm;

    @DecimalMin("15")
    @DecimalMax("65")
    private Double headCircumferenceCm;

    @DecimalMin("5")
    @DecimalMax("30")
    private Double muacCm;

    @Size(max = 500)
    private String notes;
}
