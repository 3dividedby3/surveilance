package surveilance.fish.business;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Base64.Decoder;

import javax.crypto.Cipher;

public class RsaDecrypter {
    public static final String ALGORITHM_RSA = "RSA";
    
    private static final String ENCODED_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDsjHdO49slqJoXQI6CLPHX6rtuZrmF4ddFSu4F42IEZs1152QOxXdyNvdh/4jRO1CS9DJsjvF9qG9uXvCCco5LHFIrurrKPKBhI4W8kTGo5dHEHGuR1YJK2O3vrfezcA441tgI+jR93LVasFps+CwNE5nohOQBMr+7f2B6dDzVOwIDAQAB";
    
    private static final Decoder BASE64_DECODER = Base64.getDecoder();
    
    private final Key publicKey;
    private final Cipher decryptCipher;
    
    public RsaDecrypter() {
        try {
            publicKey = initKey(ENCODED_PUBLIC_KEY);
            decryptCipher = Cipher.getInstance(ALGORITHM_RSA);
            decryptCipher.init(Cipher.DECRYPT_MODE, publicKey);
        } catch(IOException | GeneralSecurityException e) {
            System.out.println("Error while creating RSA decrypter: " + e.getMessage());
            throw new SurveilanceException(e);
        }
    }
    
    public byte[] decrypt(byte[] encodedImageData) {
        if (encodedImageData == null) {
            return null;
        }
        byte[] result = null;
        try {
            result =  decryptCipher.doFinal(BASE64_DECODER.decode(encodedImageData));
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            System.out.println("Cannot decrypt message [" + new String(encodedImageData) + "], returning null; error: " + e.getMessage());
        }
        
        return result;
    }
    
    private Key initKey(String encodedKeyData) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = BASE64_DECODER.decode(encodedKeyData);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(ALGORITHM_RSA);
        
        return kf.generatePublic(spec);
    }
}
