package in.gov.manipur.rccms.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * SMS Service
 * Mock implementation - logs SMS to console
 * For production: Integrate with SMS gateway (Twilio, MSG91, etc.)
 */
@Slf4j
@Service
public class SmsService {

    /**
     * Send SMS to mobile number
     * Currently logs to console (SMS API will be integrated later)
     * 
     * @param mobileNumber Mobile number to send SMS to
     * @param message SMS message content
     */
    public void sendSms(String mobileNumber, String message) {
        log.info("");
        log.info("╔════════════════════════════════════════════════════════════╗");
        log.info("║                    DUMMY SMS (CONSOLE)                     ║");
        log.info("╠════════════════════════════════════════════════════════════╣");
        log.info("║ TO: {}", String.format("%-52s", mobileNumber) + "║");
        log.info("║ MESSAGE: {}", String.format("%-47s", message) + "║");
        log.info("╠════════════════════════════════════════════════════════════╣");
        log.info("║ NOTE: This is a DUMMY SMS logged to console.              ║");
        log.info("║       SMS API will be integrated later.                    ║");
        log.info("╚════════════════════════════════════════════════════════════╝");
        log.info("");
        
        // TODO: Integrate with SMS gateway
        // Example: twilioService.sendSms(mobileNumber, message);
        // Example: msg91Service.sendSms(mobileNumber, message);
    }
}

