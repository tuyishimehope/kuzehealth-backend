package rw.ac.auca.kuzahealth.core.parent.enums;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;

/** Language a parent prefers to be messaged in. */
public enum Language {
    EN(Locale.ENGLISH),
    RW(new Locale("rw"));

    private final Locale locale;

    Language(Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return locale;
    }

    @JsonCreator
    public static Language parse(String value) {
        return value == null || value.isBlank() ? null : valueOf(value.trim().toUpperCase());
    }
}
