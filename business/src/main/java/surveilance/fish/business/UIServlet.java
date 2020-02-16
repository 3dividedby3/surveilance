package surveilance.fish.business;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import surveilance.fish.business.base.BaseDecSecServlet;
import surveilance.fish.business.security.AuthCookieHolder;
import surveilance.fish.security.AesDecrypter;
import surveilance.fish.security.RsaDecrypter;

public class UIServlet extends BaseDecSecServlet {
    
    public static final String INDEX_HTML = "<html><script>function goLeft() {execCommand(\"Z289bGVmdA==\");}function goForward() {execCommand(\"Z289Zm9yd2FyZA==\");}function goRight() {execCommand(\"Z289cmlnaHQ=\");}function execCommand(command) {disableAllButtons();httpGet(command);setTimeout(function reload(){window.location.reload(true)}, 25000);}function httpGet(command) {var url = \"/command?authCookie=%s&command=\" + command;console.log(url);var xmlHttp = new XMLHttpRequest();xmlHttp.open(\"GET\", url, false);try {xmlHttp.send();} catch(err) {console.log(err.message);}}function disableAllButtons() {document.getElementById(\"btnLeft\").disabled = true;document.getElementById(\"btnForward\").disabled = true;document.getElementById(\"btnRight\").disabled = true;}function enableAllButtons() {document.getElementById(\"btnLeft\").disabled = false;document.getElementById(\"btnForward\").disabled = false;document.getElementById(\"btnRight\").disabled = false;}function reloadPage() {window.location.reload(true);}</script><head/><body onload=\"enableAllButtons()\"><h1>Check the images below</h1><button id=\"btnLeft\" type=\"button\" style=\"padding: 25px\" onclick=\"goLeft()\">&lt;</button> <button id=\"btnForward\" type=\"button\" style=\"padding: 25px\" onclick=\"goForward()\">^</button> <button id=\"btnRight\" type=\"button\" style=\"padding: 25px\" onclick=\"goRight()\">&gt;</button><br>Data:%s</body></html>";
    public static final String BASE64_IMG_HTML = "<br><br><img src=\"data:image/png;base64, %s\"/>";

    private static final long serialVersionUID = -6565586545385873380L;

    private final Map<Long, String> dataToDisplay;
    
    public UIServlet(AesDecrypter aesDecrypter, RsaDecrypter rsaDecrypter, Map<Long, String> dataToDisplay) {
        super(aesDecrypter, rsaDecrypter);
        this.dataToDisplay = dataToDisplay;
    }
    
    @Override
    protected void doGetSecured(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html; charset=utf-8");
        
        String dataToDisplay = getDataToDisplay();
        try {
            response.getWriter().println(String.format(INDEX_HTML, AuthCookieHolder.getInstance().getAuthCookie(), dataToDisplay));
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (IOException e) {
            System.out.println("Error during reply, with image data, to client");
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPutSecured(HttpServletRequest request, HttpServletResponse response, String body) {
        String data;
        try {
            data = extractDataAsString(body);
        } catch (IOException e) {
            System.out.println("Received data for display cannot be parsed: " + body);
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
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