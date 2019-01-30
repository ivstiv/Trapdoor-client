package communication.security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;

public class AES {

    private final SecretKeySpec secretKey;  // the actual key generated from the string
    private final String randomString;      // string to be transmitted between edges
    private final int ivSize = 16;
    private final int keySize = 16;

    public AES() {
        this.randomString = generateJsonString();
        this.secretKey = generateKey(randomString);
    }

    public AES(String randomString) {
        this.randomString = randomString;
        this.secretKey = generateKey(randomString);
    }

    private String generateJsonString() {
        byte[] array = new byte[80];
        new SecureRandom().nextBytes(array);
        return Base64.getEncoder().encodeToString(array);
    }

    private SecretKeySpec generateKey(String randomString) {
        try {
            // hash the json string to increase entropy
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] key = randomString.getBytes(StandardCharsets.UTF_8);
            key = sha.digest(key);
            key = Arrays.copyOf(key, this.keySize); // we need only the first 16 bytes for the actual AES key
            return new SecretKeySpec(key, "AES");
        }catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getRandomString() {
        return this.randomString;
    }

    public String encrypt(String plainText) {
        // Generating IV
        byte[] iv = new byte[this.ivSize];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // Encrypt
        byte[] encrypted;
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, this.secretKey, ivParameterSpec);
            encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
            return null;
        }

        // Combine IV and encrypted part.
        byte[] encryptedIVAndText = new byte[this.ivSize + encrypted.length];
        System.arraycopy(iv, 0, encryptedIVAndText, 0, this.ivSize);
        System.arraycopy(encrypted, 0, encryptedIVAndText, this.ivSize, encrypted.length);

        return Base64.getEncoder().encodeToString(encryptedIVAndText);
    }

    public String decrypt(String encryptedText) {
        // Extract IV
        byte[] iv = new byte[this.ivSize];
        byte[] encryptedIvTextBytes = Base64.getDecoder().decode(encryptedText);
        System.arraycopy(encryptedIvTextBytes, 0, iv, 0, iv.length);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // Extract encrypted part.
        int encryptedSize = encryptedIvTextBytes.length - this.ivSize;
        byte[] encryptedBytes = new byte[encryptedSize];
        System.arraycopy(encryptedIvTextBytes, this.ivSize, encryptedBytes, 0, encryptedSize);

        // Decrypt.
        byte[] decrypted;
        try {
            Cipher cipherDecrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipherDecrypt.init(Cipher.DECRYPT_MODE, this.secretKey, ivParameterSpec);
            decrypted = cipherDecrypt.doFinal(encryptedBytes);

        } catch (Exception e ) {
            System.out.println("Error while decrypting: " + e.toString());
            return null;
        }

        return new String(decrypted, StandardCharsets.UTF_8);
    }
}
