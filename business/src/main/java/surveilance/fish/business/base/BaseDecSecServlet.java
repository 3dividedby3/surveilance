package surveilance.fish.business.base;

import java.io.IOException;

import com.fasterxml.jackson.core.type.TypeReference;

import surveilance.fish.model.DataBrick;
import surveilance.fish.security.AesDecrypter;
import surveilance.fish.security.RsaDecrypter;

public abstract class BaseDecSecServlet extends BaseSecServlet {

    private static final long serialVersionUID = -5266467755805102041L;
    
    private final AesDecrypter aesDecrypter;
    private final RsaDecrypter rsaDecrypter;

    protected BaseDecSecServlet(AesDecrypter aesDecrypter, RsaDecrypter rsaDecrypter) {
        this.aesDecrypter = aesDecrypter;
        this.rsaDecrypter = rsaDecrypter;
    }
    
    protected String extractDataAsString(String body) throws IOException, SecurityException {
        DataBrick<String> dataBrick = getObjectMapper().readValue(body, new TypeReference<DataBrick<String>>() {});
        byte[] aesKey = getRsaDecrypter().decrypt(dataBrick.getAesKey().getBytes());
        String data = new String(getAesDecrypter().decrypt(dataBrick.getPayload(), aesKey));

        return data;
    }
    
    /**
     * @return the aesDecrypter
     */
    private AesDecrypter getAesDecrypter() {
        return aesDecrypter;
    }

    /**
     * @return the rsaDecrypter
     */
    private RsaDecrypter getRsaDecrypter() {
        return rsaDecrypter;
    }
    
}
