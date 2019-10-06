package surveilance.fish.business.base;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import surveilance.fish.business.security.AuthValidator;
import surveilance.fish.business.track.Tracker;
import surveilance.fish.security.AesUtil;

abstract class BaseSurvServlet extends HttpServlet {

    private static final long serialVersionUID = 6663579614446235836L;

    private final AesUtil aesUtil;
    private final ObjectMapper objectMapper;
    private final AuthValidator authValidator;

    BaseSurvServlet(AesUtil aesUtil) {
        this.aesUtil = aesUtil;
        
        objectMapper = new ObjectMapper();
        authValidator = new AuthValidator();
    }
    
    protected abstract void doGetSecured(HttpServletRequest request, HttpServletResponse response);
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        Tracker.getInstance().trackUserData(request);
        getAuthValidator().doAuth(request);
        
        doGetSecured(request, response);
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
    AesUtil getAesUtil() {
        return aesUtil;
    }

    /**
     * @return the authValidator
     */
    private AuthValidator getAuthValidator() {
        return authValidator;
    }

}
