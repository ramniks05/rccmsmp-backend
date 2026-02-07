# Digital Signature API - Frontend Implementation Guide

API documentation for implementing the digital signature feature in the frontend application.

---

## Base URL & Authentication

- **Base URL:** `http://localhost:8080` (or your backend URL)
- **Auth:** All endpoints require JWT token in header:
  ```
  Authorization: Bearer {jwt_token}
  ```

---

## 1. Sign Document

**Endpoint:** `POST /api/cases/{caseId}/documents/{moduleType}/sign`

**Path Parameters:**

| Name        | Type   | Description                         |
|-------------|--------|-------------------------------------|
| caseId      | number | Case ID                             |
| moduleType  | string | `NOTICE` \| `ORDERSHEET` \| `JUDGEMENT` |

**Request Body:** `application/json`

```typescript
interface SignDocumentRequest {
  documentContent: string;      // HTML content to sign
  signatureMethod: 'DSC' | 'AADHAAR_OTP' | 'BIOMETRIC';
  certificatePassword?: string; // Required for DSC method
  reason: string;              // e.g. "Document finalized and approved"
  location: string;            // e.g. "District Court, Imphal East"
}
```

**Example Request:**

```http
POST /api/cases/123/documents/NOTICE/sign
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

{
  "documentContent": "<p><strong>OFFICIAL NOTICE</strong></p><p>Case Number: 123</p><p>...</p>",
  "signatureMethod": "DSC",
  "certificatePassword": "***",
  "reason": "Notice finalized and approved",
  "location": "District Court, Imphal East"
}
```

**Success Response (200 OK):**

```typescript
interface SignedDocumentResponse {
  signedDocumentId: number;
  signatureId: number;
  pdfUrl: string;           // e.g. "/api/documents/signed/456/download"
  signatureTimestamp: string; // ISO 8601, e.g. "2026-01-26T10:30:00"
  status: string;           // "SIGNED"
  fileName: string;         // e.g. "Notice_Case123_20260126_Signed.pdf"
  fileSize: number;
}

// Full response
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
  },
  "timestamp": "2026-01-26T10:30:00"
}
```

**Error Responses:**

- **503 Service Unavailable** (scaffold - feature not implemented):
  ```json
  {
    "success": false,
    "message": "Digital signature feature is not yet implemented.",
    "data": null,
    "timestamp": "..."
  }
  ```

- **401 Unauthorized:** Missing or invalid JWT token

---

## 2. Download Signed PDF

**Endpoint:** `GET /api/documents/signed/{signedDocumentId}/download`

**Path Parameters:**

| Name             | Type   | Description         |
|------------------|--------|---------------------|
| signedDocumentId | number | Signed document ID  |

**Response:** Binary PDF file

- `Content-Type: application/pdf`
- `Content-Disposition: attachment; filename="Notice_Case123_Signed.pdf"`

**Example (Angular):**

```typescript
downloadSignedPdf(signedDocumentId: number): Observable<Blob> {
  return this.http.get(
    `${this.apiUrl}/documents/signed/${signedDocumentId}/download`,
    { 
      responseType: 'blob',
      headers: { Authorization: `Bearer ${this.token}` }
    }
  );
}
```

**Example (trigger browser download):**

```typescript
this.digitalSignatureService.downloadSignedPdf(signedDocumentId).subscribe({
  next: (blob) => {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `Notice_Case${caseId}_Signed.pdf`;
    a.click();
    window.URL.revokeObjectURL(url);
  },
  error: (err) => {
    if (err.status === 503) {
      this.showError('Digital signature feature is not yet implemented.');
    }
  }
});
```

---

## 3. Verify Signature

**Endpoint:** `GET /api/documents/signed/{signedDocumentId}/verify`

**Path Parameters:**

| Name             | Type   | Description         |
|------------------|--------|---------------------|
| signedDocumentId | number | Signed document ID  |

**Success Response (200 OK):**

```typescript
interface SignatureVerificationResult {
  isValid: boolean;
  signedBy: string;
  signedAt: string;           // ISO 8601
  certificateValid: boolean;
  certificateExpiry: string;  // e.g. "2026-12-31"
  documentTampered: boolean;
}

// Full response
{
  "success": true,
  "message": "Operation successful",
  "data": {
    "isValid": true,
    "signedBy": "Officer Name",
    "signedAt": "2026-01-26T10:30:00",
    "certificateValid": true,
    "certificateExpiry": "2026-12-31",
    "documentTampered": false
  },
  "timestamp": "..."
}
```

---

## 4. Upload Officer Certificate (Admin)

**Endpoint:** `POST /api/admin/officers/{officerId}/certificate`

**Path Parameters:**

| Name      | Type   | Description |
|-----------|--------|-------------|
| officerId | number | Officer ID  |

**Request:** `multipart/form-data`

| Field           | Type   | Required | Description           |
|-----------------|--------|----------|-----------------------|
| certificateFile | File   | Yes      | .pfx or .p12 file     |
| password        | string | Yes      | Certificate password  |

**Example (Angular):**

```typescript
uploadCertificate(officerId: number, file: File, password: string): Observable<ApiResponse<CertificateUploadResponse>> {
  const formData = new FormData();
  formData.append('certificateFile', file);
  formData.append('password', password);

  return this.http.post<ApiResponse<CertificateUploadResponse>>(
    `${this.apiUrl}/admin/officers/${officerId}/certificate`,
    formData,
    { headers: { Authorization: `Bearer ${this.token}` } }
  );
}
```

**Success Response (200 OK):**

```json
{
  "success": true,
  "message": "Certificate uploaded successfully",
  "data": {
    "certificateId": 1,
    "issuer": "CCA India",
    "validFrom": "2024-01-01",
    "validUntil": "2026-12-31",
    "status": "ACTIVE"
  },
  "timestamp": "..."
}
```

---

## 5. Get My Certificate Status

**Endpoint:** `GET /api/officers/me/certificate`

**Headers:** `Authorization: Bearer {jwt_token}` (uses current officer)

**Success Response (200 OK):**

```typescript
interface CertificateStatusResponse {
  hasCertificate: boolean;
  certificateType: string;  // "DSC"
  validUntil: string;       // ISO date
  status: string;           // "ACTIVE" | "EXPIRED" | "INACTIVE"
}

// Full response
{
  "success": true,
  "message": "Operation successful",
  "data": {
    "hasCertificate": true,
    "certificateType": "DSC",
    "validUntil": "2026-12-31",
    "status": "ACTIVE"
  },
  "timestamp": "..."
}
```

**401 Unauthorized:** When officer info cannot be resolved from JWT.

---

## TypeScript Types Summary

```typescript
// Request types
export interface SignDocumentRequest {
  documentContent: string;
  signatureMethod: 'DSC' | 'AADHAAR_OTP' | 'BIOMETRIC';
  certificatePassword?: string;
  reason: string;
  location: string;
}

// Response types
export interface SignedDocumentResponse {
  signedDocumentId: number;
  signatureId: number;
  pdfUrl: string;
  signatureTimestamp: string;
  status: string;
  fileName: string;
  fileSize: number;
}

export interface SignatureVerificationResult {
  isValid: boolean;
  signedBy: string;
  signedAt: string;
  certificateValid: boolean;
  certificateExpiry: string;
  documentTampered: boolean;
}

export interface CertificateStatusResponse {
  hasCertificate: boolean;
  certificateType: string;
  validUntil: string;
  status: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T | null;
  timestamp: string;
}
```

---

## Angular Service Example

```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class DigitalSignatureService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  signDocument(
    caseId: number,
    moduleType: 'NOTICE' | 'ORDERSHEET' | 'JUDGEMENT',
    request: SignDocumentRequest
  ): Observable<SignedDocumentResponse> {
    return this.http
      .post<ApiResponse<SignedDocumentResponse>>(
        `${this.apiUrl}/cases/${caseId}/documents/${moduleType}/sign`,
        request
      )
      .pipe(map((r) => r.data!));
  }

  downloadSignedDocument(signedDocumentId: number): Observable<Blob> {
    return this.http.get(
      `${this.apiUrl}/documents/signed/${signedDocumentId}/download`,
      { responseType: 'blob' }
    );
  }

  verifySignature(signedDocumentId: number): Observable<SignatureVerificationResult> {
    return this.http
      .get<ApiResponse<SignatureVerificationResult>>(
        `${this.apiUrl}/documents/signed/${signedDocumentId}/verify`
      )
      .pipe(map((r) => r.data!));
  }

  getCertificateStatus(): Observable<CertificateStatusResponse> {
    return this.http
      .get<ApiResponse<CertificateStatusResponse>>(
        `${this.apiUrl}/officers/me/certificate`
      )
      .pipe(map((r) => r.data!));
  }

  uploadCertificate(officerId: number, file: File, password: string): Observable<any> {
    const formData = new FormData();
    formData.append('certificateFile', file);
    formData.append('password', password);
    return this.http.post(
      `${this.apiUrl}/admin/officers/${officerId}/certificate`,
      formData
    );
  }
}
```

---

## Error Handling

| Status | Meaning | Frontend Action |
|--------|---------|-----------------|
| 401 | Unauthorized | Redirect to login |
| 503 | Service Unavailable (feature not implemented) | Show "Feature not yet available" message |
| 400 | Bad Request | Display validation errors |

---

## Fake Mode for Testing

**Backend supports fake/mock implementation for testing.** Set in `application.yml`:

```yaml
app:
  digital-signature:
    fake-enabled: true   # Enables fake signing for testing
```

When `fake-enabled: true`:

- **Sign Document** – Creates mock records, stores HTML as file, returns success. No real DSC/OTP/Aadhaar required.
- **Download** – Returns the stored file (HTML wrapped as PDF-like).
- **Verify** – Returns mock verification result (isValid: true, signedBy with [FAKE] suffix).
- **Upload Certificate** – Accepts any file, stores mock certificate record.
- **Get Certificate Status** – Returns hasCertificate: true if mock cert was uploaded.

**For production:** Set `fake-enabled: false` and implement real DSC/OTP/Aadhaar signing.

---

## Current Backend Status

- Digital signature APIs support **fake mode** for testing (`fake-enabled: true`).
- When fake-enabled: All endpoints work end-to-end for frontend testing.
- Real DSC/OTP/Aadhaar implementation pending.
