package in.gov.manipur.rccms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for CAPTCHA Response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaDTO {

    private String captchaId;
    private String captchaText;
    private String imageBase64; // Optional: for future image CAPTCHA
}

