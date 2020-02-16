package surveilance.fish.business.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import surveilance.fish.business.base.BaseDecSecServlet;
import surveilance.fish.security.AesDecrypter;
import surveilance.fish.security.RsaDecrypter;

public class AuthManageServlet extends BaseDecSecServlet {

    private static final long serialVersionUID = -5850655080399392601L;

    public AuthManageServlet(AesDecrypter aesDecrypter, RsaDecrypter rsaDecrypter) {
        super(aesDecrypter, rsaDecrypter);
    }

    @Override
    protected void doPutSecured(HttpServletRequest request, HttpServletResponse response, String body) {
        String authCookieToSet;
        try {
            authCookieToSet = extractDataAsString(body);
        } catch (IOException e) {
            System.out.println("Received authCookieToSet cannot be parsed: " + body);
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        } catch (SecurityException e) {
            System.out.println("Received authCookieToSet is not properly encrypted: " + body);
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        if (authCookieToSet != null) {
            System.out.println("Received new auth cookie: " + authCookieToSet);
            AuthCookieHolder.getInstance().setAuthCookie(authCookieToSet);
        }
    }
    
    @Override
    protected boolean isAuthorised(HttpServletRequest request) {
        //this one needs to return true since it is what sets the auth cookie value, the authorization is done by correct body encryption
        return true;
    }
}
