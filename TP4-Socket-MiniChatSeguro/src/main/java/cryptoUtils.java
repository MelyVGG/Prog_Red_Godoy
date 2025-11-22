import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class cryptoUtils {
    // RSA
    public static KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        return kpg.generateKeyPair();
    }

    public static String publicKeyToBase64(PublicKey pub) {
        return Base64.getEncoder().encodeToString(pub.getEncoded());
    }

    public static PublicKey publicKeyFromBase64(String b64) throws Exception {
        byte[] data = Base64.getDecoder().decode(b64);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    public static String privateKeyToBase64(PrivateKey priv) {
        return Base64.getEncoder().encodeToString(priv.getEncoded());
    }

    public static PrivateKey privateKeyFromBase64(String b64) throws Exception {
        byte[] data = Base64.getDecoder().decode(b64);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(data);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    public static byte[] rsaEncrypt(byte[] toEncrypt, PublicKey pub) throws Exception {
        Cipher c = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        c.init(Cipher.ENCRYPT_MODE, pub);
        return c.doFinal(toEncrypt);
    }

    public static byte[] rsaDecrypt(byte[] cipherBytes, PrivateKey priv) throws Exception {
        Cipher c = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        c.init(Cipher.DECRYPT_MODE, priv);
        return c.doFinal(cipherBytes);
    }

    // AES GCM
    public static SecretKey generateAESKey(int bits) throws NoSuchAlgorithmException {
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(bits);
        return kg.generateKey();
    }

    public static class AESResult {
        public byte[] cipherText;
        public byte[] iv;
        public AESResult(byte[] ct, byte[] iv) { this.cipherText = ct; this.iv = iv; }
    }

    public static AESResult aesEncrypt(byte[] plain, SecretKey key) throws Exception {
        byte[] iv = new byte[12];
        SecureRandom r = SecureRandom.getInstanceStrong();
        r.nextBytes(iv);
        Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        c.init(Cipher.ENCRYPT_MODE, key, spec);
        byte[] ct = c.doFinal(plain);
        return new AESResult(ct, iv);
    }

    public static byte[] aesDecrypt(byte[] cipherText, SecretKey key, byte[] iv) throws Exception {
        Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        c.init(Cipher.DECRYPT_MODE, key, spec);
        return c.doFinal(cipherText);
    }

    // helpers
    public static String toBase64(byte[] b) { return Base64.getEncoder().encodeToString(b); }
    public static byte[] fromBase64(String s) { return Base64.getDecoder().decode(s); }
    public static SecretKey aesKeyFromBytes(byte[] b) { return new SecretKeySpec(b, "AES"); }
}
