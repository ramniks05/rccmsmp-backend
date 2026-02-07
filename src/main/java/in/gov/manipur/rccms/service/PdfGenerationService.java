package in.gov.manipur.rccms.service;

import in.gov.manipur.rccms.dto.DocumentMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for converting HTML to PDF.
 * Scaffold - full implementation pending (requires iText/html2pdf).
 */
@Slf4j
@Service
public class PdfGenerationService {

    /**
     * Convert HTML content to PDF.
     *
     * @param htmlContent HTML content from document
     * @param metadata    Document metadata (title, case number, etc.)
     * @return PDF as byte array
     */
    public byte[] htmlToPdf(String htmlContent, DocumentMetadata metadata) {
        throw new UnsupportedOperationException(
                "PDF generation is not yet implemented. Add iText html2pdf dependency and implement conversion.");
    }
}
