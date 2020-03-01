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
    
    public static final int DATA_IDX_HUMID = 2;
    public static final int DATA_IDX_TEMP = 1;
    public static final int DATA_IDX_IMAGE = 0;
    
    public static final String INDEX_HTML = "<html><head><script>function goLeft() {execCommand(\"Z289bGVmdA==\");}function goForward() {execCommand(\"Z289Zm9yd2FyZA==\");}function goRight() {execCommand(\"Z289cmlnaHQ=\");}function execCommand(command) {disableAllButtons();httpGet(command);setTimeout(function reload(){window.location.reload(true)}, 25000);}function httpGet(command) {var url = \"/command?authCookie=%s&command=\" + command;console.log(url);var xmlHttp = new XMLHttpRequest();xmlHttp.open(\"GET\", url, false);try {xmlHttp.send();} catch(err) {console.log(err.message);}}function disableAllButtons() {document.getElementById(\"btnLeft\").disabled = true;document.getElementById(\"btnForward\").disabled = true;document.getElementById(\"btnRight\").disabled = true;}function enableAllButtons() {document.getElementById(\"btnLeft\").disabled = false;document.getElementById(\"btnForward\").disabled = false;document.getElementById(\"btnRight\").disabled = false;}function reloadPage() {window.location.reload(true);}function drawTemp() {var temp = new Array(%s);drawArrayOnTempHumCanv(temp, \"RED\", 100);}function drawHumid() {var humid = new Array(%s);drawArrayOnTempHumCanv(humid, \"GREEN\", 100);}function drawArrayOnTempHumCanv(array, lineColor, maxVal) {var canv = document.getElementById(\"tempHumCanv\");var canvHeigth = canv.height;var xInc = canv.width/(array.length - 1);var yInc = canvHeigth/maxVal;var ctx = canv.getContext(\"2d\");ctx.font = \"10px Verdana\";ctx.strokeStyle = lineColor;ctx.beginPath();ctx.moveTo(0, canvHeigth - yInc * array[0]);ctx.fillText(array[0], 0, canvHeigth - yInc * array[0]);for (var idx = 1; idx < array.length; ++idx) {var atX = xInc * idx;var atY = canvHeigth - yInc * array[idx];ctx.lineTo(atX, atY);ctx.fillText(array[idx], atX, atY);}ctx.stroke();}</script></head><body onload=\"enableAllButtons();drawTemp();drawHumid();\"><h1>Check the images below</h1><button id=\"btnLeft\" type=\"button\" style=\"padding: 25px\" onclick=\"goLeft()\">&lt;</button><button id=\"btnForward\" type=\"button\" style=\"padding: 25px\" onclick=\"goForward()\">^</button><button id=\"btnRight\" type=\"button\" style=\"padding: 25px\" onclick=\"goRight()\">&gt;</button><br>Data:<div style=\"display: table-row\"><div style=\"display: table-cell; padding: 25px\">%s</div><div style=\"display: table-cell; padding: 25px\"><br><br><canvas id=\"tempHumCanv\" width=\"500\" height=\"300\" style=\"border:1px solid black\"></div></div></body></html>";
    public static final String BASE64_IMG_HTML = "<br><br><img src=\"data:image/png;base64, %s\"/>";
    public static final String BODY_ITEM_SEPARATOR = ";";

    private static final long serialVersionUID = -6565586545385873380L;

    private final Map<Long, String> imageDataToDisplay;
    private volatile String tempDataToDisplay = "";
    private volatile String humidDataToDisplay = "";
    
    public UIServlet(AesDecrypter aesDecrypter, RsaDecrypter rsaDecrypter, Map<Long, String> dataToDisplay) {
        super(aesDecrypter, rsaDecrypter);
        this.imageDataToDisplay = dataToDisplay;
    }
    
    @Override
    protected void doGetSecured(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html; charset=utf-8");

        String imagesDataToDisplay = getImagesDataToDisplay();
        try {
            String indexFormatted = String.format(
                    INDEX_HTML
                    , AuthCookieHolder.getInstance().getAuthCookie()
                    , tempDataToDisplay
                    , humidDataToDisplay
                    , imagesDataToDisplay);
            response.getWriter().println(indexFormatted);
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
     * the image data(first part of the data) must be received from the producer already encoded to base64
     * @param data
     */
    private void addDataToDisplay(String data) {
        String[] dataParts = data.split(BODY_ITEM_SEPARATOR);
        synchronized(imageDataToDisplay) {
            imageDataToDisplay.put(System.currentTimeMillis(), dataParts[DATA_IDX_IMAGE]);
        }
        if (dataParts.length > 1) {
            tempDataToDisplay = dataParts[DATA_IDX_TEMP];
            humidDataToDisplay = dataParts[DATA_IDX_HUMID];
        }
    }

    private String getImagesDataToDisplay() {
        StringBuilder response = new StringBuilder();
        synchronized(imageDataToDisplay) {
            String[] dataValues = imageDataToDisplay.values().toArray(new String[imageDataToDisplay.values().size()]);
            for (int idx = dataValues.length - 1; idx >= 0; --idx) {
                response.append(String.format(BASE64_IMG_HTML, dataValues[idx]));
            }
        }
        
        return response.toString();
    }

}