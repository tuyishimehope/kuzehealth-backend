package rw.ac.auca.kuzahealth.core.immunisation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VaccineScheduleItemRequest {
    @NotBlank
    @Pattern(regexp = "[A-Za-z0-9_-]{1,32}", message = "must be 1-32 letters, digits, dashes or underscores")
    private String code;

    @NotBlank
    private String vaccineName;

    @NotNull
    @PositiveOrZero
    private Integer doseNumber;

    @NotNull
    @PositiveOrZero
    private Integer dueAgeDays;

    @Size(max = 500)
    private String description;

    private Boolean active;
}
