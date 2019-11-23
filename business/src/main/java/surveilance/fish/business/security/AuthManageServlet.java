package surveilance.fish.business.security;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import surveilance.fish.business.track.Tracker;
import surveilance.fish.model.DataBrick;
import surveilance.fish.model.ViewerData;
import surveilance.fish.security.AesDecrypter;
import surveilance.fish.security.RsaDecrypter;

//TODO: create BaseDecServlet
public class AuthManageServlet extends HttpServlet {

    private static final long serialVersionUID = -5850655080399392601L;
    
    private final ObjectMapper objectMapper;
    
    private final AesDecrypter aesDecrypter;
    private final RsaDecrypter rsaDecrypter;
    
    public AuthManageServlet(AesDecrypter aesDecrypter, RsaDecrypter rsaDecrypter) {
        objectMapper = new ObjectMapper();
        
        this.aesDecrypter = aesDecrypter;
        this.rsaDecrypter = rsaDecrypter;
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ViewerData viewerData = Tracker.getInstance().trackUserData(request);
        String body = viewerData.getBody();
        if (body == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        DataBrick<String> dataBrick = objectMapper.readValue(body, new TypeReference<DataBrick<String>>() {});
        byte[] aesKey = rsaDecrypter.decrypt(dataBrick.getAesKey().getBytes());
        String authCookieToSet = new String(aesDecrypter.decrypt(dataBrick.getPayload(), aesKey));
        if (authCookieToSet != null) {
            System.out.println("Received new auth cookie: " + authCookieToSet);
            AuthCookieHolder.getInstance().setAuthCookie(authCookieToSet);
        }
    }
}
