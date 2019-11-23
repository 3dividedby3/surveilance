package surveilance.fish.business.base;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import surveilance.fish.business.security.AuthValidator;
import surveilance.fish.business.track.Tracker;

abstract class BaseSecServlet extends HttpServlet {

    private static final long serialVersionUID = 6663579614446235836L;

    private final AuthValidator authValidator;

    BaseSecServlet() {
        authValidator = new AuthValidator();
    }
    
    protected void doGetSecured(HttpServletRequest request, HttpServletResponse response) {
        ;
    }
    
    @Override
    protected final void doGet(HttpServletRequest request, HttpServletResponse response) {
        track(request);
        getAuthValidator().doAuth(request);
        
        doGetSecured(request, response);
    }

    protected void track(HttpServletRequest request) {
        Tracker.getInstance().trackUserData(request);
    }

    /**
     * @return the authValidator
     */
    private AuthValidator getAuthValidator() {
        return authValidator;
    }

}
