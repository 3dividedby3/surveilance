package surveilance.fish.business.security;

public class AuthCookieHolder {

    private volatile String authCookie;

    private static final AuthCookieHolder INSTANCE = new AuthCookieHolder();

    private AuthCookieHolder() {
        authCookie = null;
    }

    public static AuthCookieHolder getInstance() {
        return INSTANCE;
    }

    /**
     * @return the authCookie
     */
    public String getAuthCookie() {
        return authCookie;
    }

    /**
     * @param authCookie the authCookie to set
     */
    public void setAuthCookie(String authCookie) {
        this.authCookie = authCookie;
    }
}
