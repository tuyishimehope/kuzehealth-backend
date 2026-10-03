package rw.ac.auca.kuzahealth.core.anc;

import java.time.LocalTime;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AncScheduleRequest {
    @NotNull
    private UUID healthWorkerId;

    @NotBlank
    private String location;

    @NotBlank
    private String modeOfCommunication;

    /** Local time of day for the visits; defaults to 09:00. */
    private LocalTime time;
}
