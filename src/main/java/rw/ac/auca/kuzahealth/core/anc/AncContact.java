package rw.ac.auca.kuzahealth.core.anc;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * One recommended antenatal contact.
 *
 * @param gestationalWeek week of pregnancy at which the contact is recommended
 * @param visitId         the visit matched to this contact, if any
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AncContact(int number, int gestationalWeek, LocalDate dueDate, AncContactStatus status, UUID visitId) {
}
