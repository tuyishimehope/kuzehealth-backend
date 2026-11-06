package rw.ac.auca.kuzahealth.core.exception;

/** The caller could not be authenticated, for example because of wrong credentials. */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
