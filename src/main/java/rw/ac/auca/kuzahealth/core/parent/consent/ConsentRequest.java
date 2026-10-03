package rw.ac.auca.kuzahealth.core.parent.consent;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ConsentRequest {
    @NotNull
    private ConsentType type;

    @NotNull
    private Boolean granted;

    @Size(max = 500)
    private String note;
}
