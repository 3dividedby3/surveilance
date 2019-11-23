package surveilance.fish.business.base;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import surveilance.fish.model.DataBrick;
import surveilance.fish.security.AesEncrypter;
import surveilance.fish.security.AesUtil;
import surveilance.fish.security.RsaEncrypter;

public abstract class BaseEncServlet extends BaseSecServlet {

    private static final long serialVersionUID = -4267910856903669854L;
    
    private final RsaEncrypter rsaEncrypter;
    private final AesEncrypter aesEncrypter;
    
    private final AesUtil aesUtil;
    private final ObjectMapper objectMapper;
    
    protected BaseEncServlet(AesEncrypter aesEncrypter, RsaEncrypter rsaEncrypter, AesUtil aesUtil) {
        this.aesEncrypter = aesEncrypter;
        this.rsaEncrypter = rsaEncrypter;
        this.aesUtil = aesUtil;
        
        objectMapper = new ObjectMapper();
    }
    
    protected <T> DataBrick<T> createDataBrick(T data) throws IOException {
        DataBrick<T> dataBrick = new DataBrick<>();
        String dataAsString = getObjectMapper().writeValueAsString(data);
        byte[] key = getAesUtil().createAesKey();
        dataBrick.setAesKey(getRsaEncrypter().encryptAndEncode(key));
        dataBrick.setPayload(getAesEncrypter().encryptAndEncode(dataAsString, key));
        
        return dataBrick;
    }

    /**
     * @return the objectMapper
     */
    protected ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * @return the aesUtil
     */
    private AesUtil getAesUtil() {
        return aesUtil;
    }

    /**
     * @return the rsaEncrypter
     */
    private RsaEncrypter getRsaEncrypter() {
        return rsaEncrypter;
    }

    /**
     * @return the aesEncrypter
     */
    private AesEncrypter getAesEncrypter() {
        return aesEncrypter;
    }

}
