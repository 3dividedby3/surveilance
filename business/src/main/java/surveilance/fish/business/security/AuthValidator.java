package surveilance.fish.business.security;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class AuthValidator {

    public static final String NAME_AUTH_COOKIE = "authCookie";

    public void doAuth(HttpServletRequest request) throws SecurityException {
        String securedAuthCookie = AuthCookieHolder.getInstance().getAuthCookie();
        if (securedAuthCookie == null) {
            System.out.println("Secured [" + NAME_AUTH_COOKIE + "] not set, call http put first!");
            throw new SecurityException("Secured cookie not set");
        }
        
        //using request parameter as fall-back when cookies cannot be set
        if (isAuthCookieValid(request.getParameter(NAME_AUTH_COOKIE))) {
            return;
        }
        
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length <= 0) {
            throw new SecurityException("Missing cookies");
        }
        
        for (Cookie cookie : cookies) {
            if (NAME_AUTH_COOKIE.equals(cookie.getName())) {
                if (!isAuthCookieValid(cookie.getValue())) {
                    throw new SecurityException("Invalid [" + NAME_AUTH_COOKIE + "] received");
                } else {
                    return;
                }
            }
        }
        
        System.out.println("Missing [" + NAME_AUTH_COOKIE + "]");
        throw new SecurityException("Missing cookie");
    }

    private boolean isAuthCookieValid(String receivedCookieValue) {
        String securedAuthCookie = AuthCookieHolder.getInstance().getAuthCookie();
        if (securedAuthCookie.equals(receivedCookieValue)) {
            //correct auth cookie
            return true;
        }

        System.out.println("Invalid [" + NAME_AUTH_COOKIE + "] received: [" + receivedCookieValue + "], but must be: [" + securedAuthCookie + "]");
        //TODO: if auth is invalid then delay a couple of seconds to slow down brute force
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        return false;

    }
}
