package communication.security;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSA {

    private KeyPair keys;
    private PublicKey remotePublicKey;

    public RSA() throws Exception {
        keys = generateKeyPair();
    }

    private KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048, new SecureRandom());

        return generator.generateKeyPair();
    }

    public String encrypt(String plainText) throws Exception {
        if(remotePublicKey != null) {
            Cipher encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, remotePublicKey);

            byte[] cipherText = encryptCipher.doFinal(plainText.getBytes("UTF8"));

            return Base64.getEncoder().encodeToString(cipherText);
        }
        else{
            throw new Exception("remotePublicKey not initialised!");
        }
    }

    public String decrypt(String cipherText) throws Exception {
        byte[] bytes = Base64.getDecoder().decode(cipherText);

        Cipher decryptCipher = Cipher.getInstance("RSA");
        decryptCipher.init(Cipher.DECRYPT_MODE, keys.getPrivate());

        return new String(decryptCipher.doFinal(bytes), "UTF8");
    }

    public String sign(String plainText) throws Exception {
        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(keys.getPrivate());
        privateSignature.update(plainText.getBytes("UTF8"));

        byte[] signature = privateSignature.sign();

        return Base64.getEncoder().encodeToString(signature);
    }

    public boolean verify(String plainText, String signature) throws Exception {
        Signature publicSignature = Signature.getInstance("SHA256withRSA");
        publicSignature.initVerify(remotePublicKey);
        publicSignature.update(plainText.getBytes("UTF8"));

        byte[] signatureBytes = Base64.getDecoder().decode(signature);

        return publicSignature.verify(signatureBytes);
    }

    public boolean setRemotePublicKey(String keyBase64) throws NoSuchAlgorithmException {
        byte[] bytes = Base64.getDecoder().decode(keyBase64);
        X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(bytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");

        try {
            this.remotePublicKey = kf.generatePublic(X509publicKey);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    public String getPublicKeyBase64() {
        return Base64.getEncoder().encodeToString(keys.getPublic().getEncoded());
    }
}
