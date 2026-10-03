package rw.ac.auca.kuzahealth.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public final class Dates {

    private Dates() {
    }

    /** Converts a legacy date to a calendar date without shifting date-only values across time zones. */
    public static LocalDate toLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        if (date instanceof java.sql.Date dateOnly) {
            return dateOnly.toLocalDate();
        }
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static Date toDate(LocalDate date) {
        return date == null ? null : java.sql.Date.valueOf(date);
    }
}
