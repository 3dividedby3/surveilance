package surveilance.fish.business.base;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import surveilance.fish.business.ServletUtils;
import surveilance.fish.business.security.AuthValidator;
import surveilance.fish.business.track.Tracker;

abstract class BaseSecServlet extends HttpServlet {

    private static final long serialVersionUID = 6663579614446235836L;

    private final AuthValidator authValidator;
    private final ObjectMapper objectMapper;
    
    private final ServletUtils servletUtils;

    BaseSecServlet() {
        authValidator = new AuthValidator();
        objectMapper = new ObjectMapper();
        servletUtils = new ServletUtils();
    }

    @Override
    protected final void doGet(HttpServletRequest request, HttpServletResponse response) {
        if (isNotAuthorised(request)) {
            try {
                track(request, servletUtils.readBody(request));
            } catch (IOException e) {
                System.out.println("Error during read body");
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        doGetSecured(request, response);
    }

    protected void doGetSecured(HttpServletRequest request, HttpServletResponse response) {
        ;
    }
    
    @Override
    protected final void doPut(HttpServletRequest request, HttpServletResponse response) {
        String body;
        try {
            body = servletUtils.readBody(request);
        } catch (IOException e) {
            System.out.println("Error during read body");
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        System.out.println("Received data: " + body);
        
        if (isNotAuthorised(request)) {
            track(request, body);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        if (body == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        doPutSecured(request, response, body);
    }

    protected void doPutSecured(HttpServletRequest request, HttpServletResponse response, String body) {
        ;
    }
    
    protected boolean isAuthorised(HttpServletRequest request) {
        boolean isAuthorised;
        try {
            getAuthValidator().doAuth(request);
            isAuthorised = true;
        } catch (SecurityException secExc) {
            System.out.println("Request unauthorised");
            secExc.printStackTrace();
            isAuthorised = false;
        }
        
        return isAuthorised;
    }

    private boolean isNotAuthorised(HttpServletRequest request) {
        return !isAuthorised(request);
    }

    protected void track(HttpServletRequest request, String body) {
        Tracker.getInstance().trackUserData(request, body);
    }

    /**
     * @return the objectMapper
     */
    protected ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * @return the authValidator
     */
    private AuthValidator getAuthValidator() {
        return authValidator;
    }

}
