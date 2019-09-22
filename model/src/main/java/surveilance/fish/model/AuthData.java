package surveilance.fish.model;

public class AuthData {

    private String authCookie;
    
    public AuthData(String authCookie) {
        this.authCookie = authCookie;
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
