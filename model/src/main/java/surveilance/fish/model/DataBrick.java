package surveilance.fish.model;

public class DataBrick {

    /** AES key used to encrypt the payload. The key is encrypted with a private RSA key and Base64 encoded. */
    private String aesKey;
    
    /** Encrypted with the AES key and Base64 encoded. */
    private String payload;

    public String getAesKey() {
        return aesKey;
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }
    
    public void setAesKey(byte[] aesKey) {
        setAesKey(new String(aesKey));
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
    
    public void setPayload(byte[] payload) {
        setPayload(new String(payload));
    }
}
