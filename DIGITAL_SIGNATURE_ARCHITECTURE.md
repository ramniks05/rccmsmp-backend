# Digital Signature Architecture for Documents

## Overview

Implementation plan for digital signatures on Notice, Ordersheet, and Judgement documents before finalization.

**Current Status:** Scaffold implemented. Fake mode available for testing (`app.digital-signature.fake-enabled=true`). Real DSC/OTP/Aadhaar implementation pending.

---

## High-Level Flow

```
1. Officer creates document (Notice/Ordersheet/Judgement)
   ↓
2. Officer reviews and clicks "Finalize & Sign"
   ↓
3. System prompts for digital signature (OTP/Certificate/Biometric)
   ↓
4. Backend applies digital signature to document
   ↓
5. Document is converted to PDF with embedded signature
   ↓
6. Signed PDF is stored permanently
   ↓
7. Document status changes to "FINALIZED"
   ↓
8. Document can be downloaded with valid signature
```

---

## API Contracts

### 1. Sign Document

```
POST /api/cases/{caseId}/documents/{moduleType}/sign
Content-Type: application/json

Request:
{
  "documentContent": "<html>...</html>",
  "signatureMethod": "DSC",
  "certificatePassword": "***",
  "reason": "Document finalized and approved",
  "location": "District Court, Imphal East"
}

Response (when implemented):
{
  "success": true,
  "message": "Document signed successfully",
  "data": {
    "signedDocumentId": 456,
    "signatureId": 789,
    "pdfUrl": "/api/documents/signed/456/download",
    "signatureTimestamp": "2026-01-26T10:30:00",
    "status": "SIGNED",
    "fileName": "Notice_Case123_20260126_Signed.pdf",
    "fileSize": 245678
  }
}
```

### 2. Download Signed PDF

```
GET /api/documents/signed/{signedDocumentId}/download

Response: Binary PDF file
Content-Type: application/pdf
Content-Disposition: attachment; filename="Notice_Case123_Signed.pdf"
```

### 3. Verify Signature

```
GET /api/documents/signed/{signedDocumentId}/verify

Response:
{
  "success": true,
  "data": {
    "isValid": true,
    "signedBy": "Officer Name",
    "signedAt": "2026-01-26T10:30:00",
    "certificateValid": true,
    "certificateExpiry": "2026-12-31",
    "documentTampered": false
  }
}
```

### 4. Upload Officer Certificate

```
POST /api/admin/officers/{officerId}/certificate
Content-Type: multipart/form-data
Body: certificateFile (.pfx/.p12), password

Response:
{
  "success": true,
  "message": "Certificate uploaded successfully",
  "data": {
    "certificateId": 1,
    "issuer": "CCA India",
    "validFrom": "2024-01-01",
    "validUntil": "2026-12-31",
    "status": "ACTIVE"
  }
}
```

### 5. Get Certificate Status

```
GET /api/officers/me/certificate

Response:
{
  "success": true,
  "data": {
    "hasCertificate": true,
    "certificateType": "DSC",
    "validUntil": "2026-12-31",
    "status": "ACTIVE"
  }
}
```

---

## Database Schema

### officer_certificates

| Column            | Type         | Description                        |
|-------------------|--------------|------------------------------------|
| id                | BIGSERIAL    | Primary key                        |
| officer_id        | BIGINT       | FK to officers                     |
| certificate_type  | VARCHAR(50)  | DSC, AADHAAR, BIOMETRIC            |
| certificate_data  | BYTEA        | Encrypted .pfx/.p12                |
| password_hash     | VARCHAR(255) | BCrypt hash of certificate password|
| issuer            | VARCHAR(500) | Certificate issuer                 |
| subject           | VARCHAR(500) | Certificate subject                |
| serial_number     | VARCHAR(255) | Certificate serial                 |
| valid_from        | TIMESTAMP    | Valid from                         |
| valid_until       | TIMESTAMP    | Valid until                        |
| is_active         | BOOLEAN      | Active flag                        |
| created_at        | TIMESTAMP    |                                    |
| updated_at        | TIMESTAMP    |                                    |

### digital_signatures

| Column              | Type         | Description                     |
|---------------------|--------------|---------------------------------|
| id                  | BIGSERIAL    | Primary key                     |
| document_id         | BIGINT       | case_documents.id               |
| document_type       | VARCHAR(50)  | NOTICE, ORDERSHEET, JUDGEMENT   |
| case_id             | BIGINT       | Case ID                         |
| officer_id          | BIGINT       | Signing officer                 |
| certificate_id      | BIGINT       | FK to officer_certificates      |
| signature_method    | VARCHAR(50)  | DSC, AADHAAR_OTP, BIOMETRIC     |
| signature_data      | BYTEA        | Signature bytes                 |
| signature_timestamp | TIMESTAMP    |                                 |
| reason              | VARCHAR(500) |                                 |
| location            | VARCHAR(255) |                                 |
| ip_address          | VARCHAR(50)  |                                 |
| verification_status | VARCHAR(50)  | PENDING, VALID, INVALID         |
| created_at          | TIMESTAMP    |                                 |

### signed_documents

| Column               | Type         | Description               |
|----------------------|--------------|---------------------------|
| id                   | BIGSERIAL    | Primary key               |
| original_document_id | BIGINT       | case_documents.id         |
| document_type        | VARCHAR(50)  | NOTICE, ORDERSHEET, JUDGEMENT |
| case_id              | BIGINT       | Case ID                   |
| signed_pdf_path      | VARCHAR(500) | File system path          |
| file_name            | VARCHAR(255) |                           |
| file_size            | BIGINT       |                           |
| pdf_hash             | VARCHAR(255) | SHA-256 hash              |
| signature_id         | BIGINT       | FK to digital_signatures  |
| status               | VARCHAR(50)  | SIGNED, FINALIZED         |
| created_by           | BIGINT       | Officer ID                |
| created_at           | TIMESTAMP    |                           |

### case_documents (updated)

| Column             | Type    | Description                    |
|--------------------|---------|--------------------------------|
| signature_id       | BIGINT  | FK to digital_signatures (nullable) |
| signed_document_id | BIGINT  | FK to signed_documents (nullable)   |

---

## Services to Implement

| Service                  | Method                    | Status    |
|--------------------------|---------------------------|-----------|
| DigitalSignatureService  | signDocument()            | Stub      |
| DigitalSignatureService  | verifySignature()         | Stub      |
| PdfGenerationService     | htmlToPdf()               | Stub      |
| CertificateManagementService | storeCertificate()   | Stub      |
| CertificateManagementService | getCertificateStatus() | Stub      |

---

## Maven Dependencies (to add when implementing)

```xml
<!-- iText 7 for PDF generation and signing -->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itext7-core</artifactId>
    <version>7.2.5</version>
</dependency>

<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>html2pdf</artifactId>
    <version>4.0.5</version>
</dependency>

<!-- Bouncy Castle for cryptography -->
<dependency>
    <groupId>org.bouncycastle</groupId>
    <artifactId>bcprov-jdk15on</artifactId>
    <version>1.70</version>
</dependency>

<dependency>
    <groupId>org.bouncycastle</groupId>
    <artifactId>bcpkix-jdk15on</artifactId>
    <version>1.70</version>
</dependency>
```

---

## Configuration Keys

| Key                              | Default                        | Description                |
|----------------------------------|--------------------------------|----------------------------|
| app.digital-signature.enabled    | false                          | Enable real digital signatures |
| app.digital-signature.fake-enabled | true                         | **Testing only** - Simulates signing without DSC/OTP/Aadhaar |
| app.digital-signature.storage-path | ./data/signed-documents/     | Path for signed PDFs       |

---

## Implementation Phases

### Phase 1: Basic Setup (Week 1)
- Add Maven dependencies (iText, Bouncy Castle)
- Implement PdfGenerationService.htmlToPdf()
- Implement CertificateManagementService.storeCertificate()
- Implement CertificateManagementService.getCertificateStatus()
- Wire certificate upload and status APIs

### Phase 2: Signature Implementation (Week 2)
- Implement DigitalSignatureService.signDocument()
- Implement PDF signing with iText PdfSigner
- Implement file storage for signed PDFs
- Wire sign, download, verify APIs

### Phase 3: Integration (Week 3)
- Update CaseDocument flow with signature status
- Add audit logging
- Security hardening (encryption, access control)
- Testing and documentation

---

## Signature Methods

### DSC (Digital Signature Certificate)
- Officer uploads .pfx/.p12 file
- Password required to unlock for signing
- Most secure and legally valid

### Aadhaar e-Sign (Future)
- OTP sent to registered mobile
- ESP (e-Sign Service Provider) integration
- No certificate needed

### Biometric (Future)
- Court premises with biometric devices

---

## Security Considerations

- Encrypt certificates at rest (AES-256)
- Hash certificate passwords (BCrypt)
- Never log passwords or certificate data
- Verify certificate chain and expiry
- Rate limit signature operations
- Audit trail for all signature attempts
