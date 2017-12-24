package surveilance.fish.business;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Base64.Decoder;

import javax.crypto.Cipher;

public class RsaDecrypter {
    public static final String ALGORITHM_RSA = "RSA";
    private static final Decoder BASE64_DECODER = Base64.getDecoder();
    
    private final Key publicKey;
    private final Cipher decryptCipher;
    
    public RsaDecrypter(String encodedPublicKey) {
        try {
            publicKey = initKey(encodedPublicKey);
            decryptCipher = Cipher.getInstance(ALGORITHM_RSA);
            decryptCipher.init(Cipher.DECRYPT_MODE, publicKey);
        } catch(IOException | GeneralSecurityException e) {
            System.out.println("Error while creating RSA decrypter: " + e.getMessage());
            throw new SurveilanceException(e);
        }
    }
    
    public byte[] decrypt(byte[] encodedKey) {
        if (encodedKey == null) {
            return null;
        }
        byte[] result = null;
        try {
            result =  decryptCipher.doFinal(BASE64_DECODER.decode(encodedKey));
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            System.out.println("Cannot decrypt message [" + new String(encodedKey) + "], returning null; error: " + e.getMessage());
        }
        
        return result;
    }
    
    private Key initKey(String encodedKeyData) throws IOException, GeneralSecurityException {
        byte[] keyBytes = BASE64_DECODER.decode(encodedKeyData);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(ALGORITHM_RSA);
        
        return kf.generatePublic(spec);
    }
}
