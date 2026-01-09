package in.gov.manipur.rccms.exception;

/**
 * Exception thrown when CAPTCHA validation fails
 */
public class InvalidCaptchaException extends RuntimeException {
    public InvalidCaptchaException(String message) {
        super(message);
    }
}

