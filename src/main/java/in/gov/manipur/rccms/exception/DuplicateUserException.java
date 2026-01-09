package in.gov.manipur.rccms.exception;

/**
 * Exception thrown when attempting to register a user with duplicate email/mobile/aadhar
 */
public class DuplicateUserException extends RuntimeException {
    public DuplicateUserException(String message) {
        super(message);
    }
}

