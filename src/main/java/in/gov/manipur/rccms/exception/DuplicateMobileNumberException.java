package in.gov.manipur.rccms.exception;

/**
 * Exception thrown when attempting to register with a mobile number that already exists
 */
public class DuplicateMobileNumberException extends RuntimeException {

    public DuplicateMobileNumberException(String message) {
        super(message);
    }

    public DuplicateMobileNumberException(String message, Throwable cause) {
        super(message, cause);
    }
}

