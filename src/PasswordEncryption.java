import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class PasswordEncryption {
    
    // Hardcoded 16-byte key for AES-128 (basic implementation, not secure)
    private static final String ENCRYPTION_KEY = "MySecretKey12345"; // 16 characters
    private static final String ALGORITHM = "AES";
    
    public static String encrypt(String password) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(ENCRYPTION_KEY.getBytes(), 0, 
                    ENCRYPTION_KEY.getBytes().length, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            
            byte[] encryptedBytes = cipher.doFinal(password.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
            
        } catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            System.err.println("Error encrypting password: " + e.getMessage());
            return null;
        }
    }

    public static String decrypt(String encryptedPassword) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(ENCRYPTION_KEY.getBytes(), 0, 
                    ENCRYPTION_KEY.getBytes().length, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedPassword);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            
            return new String(decryptedBytes);
            
        } catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            System.err.println("Error decrypting password: " + e.getMessage());
            return null;
        }
    }
}
