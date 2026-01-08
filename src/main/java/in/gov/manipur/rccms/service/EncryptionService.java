package in.gov.manipur.rccms.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Encryption Service for sensitive data (Aadhaar numbers)
 * Uses AES encryption for data at rest
 * 
 * Note: In production, use a proper key management system (KMS) or environment-based keys
 */
@Service
public class EncryptionService {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";
    
    // Default key - In production, this should be from environment variable or KMS
    @Value("${app.encryption.key:MySecretKey12345}")
    private String encryptionKey;

    /**
     * Encrypt sensitive data (Aadhaar number)
     * @param plainText the text to encrypt
     * @return encrypted Base64 string
     */
    public String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return null;
        }
        
        try {
            // Ensure key is 16 bytes for AES-128
            byte[] keyBytes = getKeyBytes();
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
            
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting data", e);
        }
    }

    /**
     * Decrypt sensitive data (Aadhaar number)
     * @param encryptedText the encrypted Base64 string
     * @return decrypted plain text
     */
    public String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return null;
        }
        
        try {
            byte[] keyBytes = getKeyBytes();
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
            
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting data", e);
        }
    }

    /**
     * Get key bytes (16 bytes for AES-128)
     * In production, this should use a proper key derivation function
     */
    private byte[] getKeyBytes() {
        byte[] key = encryptionKey.getBytes(StandardCharsets.UTF_8);
        byte[] keyBytes = new byte[16];
        System.arraycopy(key, 0, keyBytes, 0, Math.min(key.length, 16));
        return keyBytes;
    }
}

