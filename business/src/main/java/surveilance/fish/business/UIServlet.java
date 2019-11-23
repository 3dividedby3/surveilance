package surveilance.fish.business;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import surveilance.fish.business.security.AuthCookieHolder;
import surveilance.fish.business.security.AuthValidator;
import surveilance.fish.business.track.Tracker;
import surveilance.fish.model.DataBrick;
import surveilance.fish.model.ViewerData;
import surveilance.fish.security.AesDecrypter;
import surveilance.fish.security.RsaDecrypter;

public class UIServlet extends HttpServlet {
    
    public static final String INDEX_HTML = "<html><script>function goLeft() {execCommand(\"Z289bGVmdA==\");}function goForward() {execCommand(\"Z289Zm9yd2FyZA==\");}function goRight() {execCommand(\"Z289cmlnaHQ=\");}function execCommand(command) {disableAllButtons();httpGet(command);setTimeout(function reload(){window.location.reload(true)}, 25000);}function httpGet(command) {var url = \"/command?authCookie=%s&command=\" + command;console.log(url);var xmlHttp = new XMLHttpRequest();xmlHttp.open(\"GET\", url, false);try {xmlHttp.send();} catch(err) {console.log(err.message);}}function disableAllButtons() {document.getElementById(\"btnLeft\").disabled = true;document.getElementById(\"btnForward\").disabled = true;document.getElementById(\"btnRight\").disabled = true;}function enableAllButtons() {document.getElementById(\"btnLeft\").disabled = false;document.getElementById(\"btnForward\").disabled = false;document.getElementById(\"btnRight\").disabled = false;}function reloadPage() {window.location.reload(true);}</script><head/><body onload=\"enableAllButtons()\"><h1>Check the images below</h1><button id=\"btnLeft\" type=\"button\" style=\"padding: 25px\" onclick=\"goLeft()\">&lt;</button> <button id=\"btnForward\" type=\"button\" style=\"padding: 25px\" onclick=\"goForward()\">^</button> <button id=\"btnRight\" type=\"button\" style=\"padding: 25px\" onclick=\"goRight()\">&gt;</button><br>Data:%s</body></html>";
    public static final String BASE64_IMG_HTML = "<br><br><img src=\"data:image/png;base64, %s\"/>";

    private static final long serialVersionUID = -6565586545385873380L;

    private final AesDecrypter aesDecrypter;
    private final RsaDecrypter rsaDecrypter;
    private final ObjectMapper objectMapper;
    private final Map<Long, String> dataToDisplay;
    private final AuthValidator authValidator;
    
    public UIServlet(AesDecrypter aesDecrypter, RsaDecrypter rsaDecrypter, Map<Long, String> dataToDisplay) {
        this.aesDecrypter = aesDecrypter;
        this.rsaDecrypter = rsaDecrypter;
        objectMapper = new ObjectMapper();
        this.dataToDisplay = dataToDisplay;
        authValidator = new AuthValidator();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response ) throws IOException  {
        Tracker.getInstance().trackUserData(request);
        authValidator.doAuth(request);
        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        
        String dataToDisplay = getDataToDisplay();
        response.getWriter().println(String.format(INDEX_HTML, AuthCookieHolder.getInstance().getAuthCookie(), dataToDisplay));
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ViewerData viewerData = Tracker.getInstance().trackUserData(request);
        authValidator.doAuth(request);
        String body = viewerData.getBody();
        System.out.println("Received data: " + body);
        if (body == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        DataBrick<String> dataBrick = objectMapper.readValue(body, new TypeReference<DataBrick<String>>() {});
        byte[] aesKey = rsaDecrypter.decrypt(dataBrick.getAesKey().getBytes());
        String data = new String(aesDecrypter.decrypt(dataBrick.getPayload(), aesKey));
        if (data != null) {
            System.out.println("Adding data to be displayed: " + data);
            addDataToDisplay(data);
        }
    }

    /**
     * the data must be received from the producer already encoded to base64
     * @param data
     */
    private void addDataToDisplay(String data) {
        synchronized(dataToDisplay) {
            dataToDisplay.put(System.currentTimeMillis(), data);
        }
    }

    private String getDataToDisplay() {
        StringBuilder response = new StringBuilder();
        synchronized(dataToDisplay) {
            String[] dataValues = dataToDisplay.values().toArray(new String[dataToDisplay.values().size()]);
            for (int idx = dataValues.length - 1; idx >= 0; --idx) {
                response.append(String.format(BASE64_IMG_HTML, dataValues[idx]));
            }
        }
        
        return response.toString();
    }
    
}