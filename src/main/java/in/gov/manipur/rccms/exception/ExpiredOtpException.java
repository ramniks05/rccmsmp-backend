package in.gov.manipur.rccms.exception;

/**
 * Exception thrown when OTP is expired
 */
public class ExpiredOtpException extends RuntimeException {
    public ExpiredOtpException(String message) {
        super(message);
    }
}

