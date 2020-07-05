package surveilance.fish.business;

import java.io.IOException;
import java.util.Deque;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import surveilance.fish.business.base.BaseDecSecServlet;
import surveilance.fish.business.security.AuthCookieHolder;
import surveilance.fish.security.AesDecrypter;
import surveilance.fish.security.RsaDecrypter;

public class UIServlet extends BaseDecSecServlet {

    public static final int DATA_IDX_IMAGE = 0;
    public static final int DATA_IDX_TEMP_1 = 1;
    public static final int DATA_IDX_HUMID_1 = 2;
    public static final int DATA_IDX_TEMP_2 = 3;
    public static final int DATA_IDX_HUMID_2 = 4;
    
    public static final String INDEX_HTML = "<html><head><script>function goLeft() {execCommand(\"Z289bGVmdA==\");}function goRight() {execCommand(\"Z289cmlnaHQ=\");}function execCommand(command) {disableAllButtons();httpGet(command);setTimeout(function reload(){window.location.reload(true)}, 6000);}function httpGet(command) {var url = \"/command?authCookie=%s&command=\" + command;var xmlHttp = new XMLHttpRequest();xmlHttp.open(\"GET\", url, false);try {xmlHttp.send();} catch(err) {console.log(err.message);} }function disableAllButtons() {document.getElementById(\"btnLeft\").disabled = true;document.getElementById(\"btnRight\").disabled = true;}function enableAllButtons() {document.getElementById(\"btnLeft\").disabled = false;document.getElementById(\"btnRight\").disabled = false;}function reloadPage() {window.location.reload(true);}function drawTemp1() {var temp = new Array(%s);drawArrayOnTempHumCanv(1, temp, \"RED\", 100);}function drawHumid1() {var humid = new Array(%s);drawArrayOnTempHumCanv(1, humid, \"GREEN\", 100);} function drawTemp2() {var temp = new Array(%s);drawArrayOnTempHumCanv(2, temp, \"RED\", 100);}function drawHumid2() {var humid = new Array(%s);drawArrayOnTempHumCanv(2, humid, \"GREEN\", 100);}function drawArrayOnTempHumCanv(canvasIdx, array, lineColor, maxVal) {var canv = document.getElementById(\"tempHumCanv\" + canvasIdx);var canvHeigth = canv.height;var xInc = canv.width/(array.length - 1);var yInc = canvHeigth/maxVal;var ctx = canv.getContext(\"2d\");ctx.font = \"10px Verdana\";ctx.strokeStyle = lineColor;ctx.beginPath();ctx.moveTo(0, canvHeigth - yInc * array[0]);ctx.fillText(array[0], 0, canvHeigth - yInc * array[0]);for (var idx = 1; idx < array.length; ++idx) {var atX = xInc * idx;var atY = canvHeigth - yInc * array[idx];ctx.lineTo(atX, atY);if (idx == array.length - 1) {ctx.fillText(array[idx], atX - 15, atY);} else {ctx.fillText(array[idx], atX, atY);}}ctx.stroke();}</script></head><body onload=\"enableAllButtons();drawTemp1();drawHumid1();drawTemp2();drawHumid2();\"><h1>Welcome</h1><button id=\"btnLeft\" type=\"button\" style=\"padding: 25px\" onclick=\"goLeft()\">&lt;</button><button id=\"btnRight\" type=\"button\" style=\"padding: 25px\" onclick=\"goRight()\">&gt;</button><div style=\"display: table-row\"><div style=\"display: table-cell; padding: 25px\">%s</div><div style=\"display: table-cell; padding: 25px\"><div style=\"padding: 25px\"><br><br><canvas id=\"tempHumCanv1\" width=\"500\" height=\"300\" style=\"border:1px solid black\"></div><div style=\"padding: 25px\"><br><br><canvas id=\"tempHumCanv2\" width=\"500\" height=\"300\" style=\"border:1px solid black\"></div></div></div></body></html>";
    public static final String BASE64_IMG_HTML = "<br><br><img src=\"data:image/png;base64, %s\"/>";
    public static final String BODY_ITEM_SEPARATOR = ";";

    private static final long serialVersionUID = -6565586545385873380L;

    private final Deque<String> imageDataToDisplay;
    private volatile String temp1DataToDisplay = "";
    private volatile String humid1DataToDisplay = "";
    private volatile String temp2DataToDisplay = "";
    private volatile String humid2DataToDisplay = "";
    
    public UIServlet(AesDecrypter aesDecrypter, RsaDecrypter rsaDecrypter, Deque<String> dataToDisplay) {
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
                    , temp1DataToDisplay
                    , humid1DataToDisplay
                    , temp2DataToDisplay
                    , humid2DataToDisplay
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
            imageDataToDisplay.add(dataParts[DATA_IDX_IMAGE]);
        }
        if (dataParts.length > 1) {
            temp1DataToDisplay = dataParts[DATA_IDX_TEMP_1];
            humid1DataToDisplay = dataParts[DATA_IDX_HUMID_1];
            temp2DataToDisplay = dataParts[DATA_IDX_TEMP_2];
            humid2DataToDisplay = dataParts[DATA_IDX_HUMID_2];
        }
    }

    private String getImagesDataToDisplay() {
        StringBuilder response = new StringBuilder();
        synchronized(imageDataToDisplay) {
            String[] dataValues = imageDataToDisplay.toArray(new String[imageDataToDisplay.size()]);
            for (int idx = dataValues.length - 1; idx >= 0; --idx) {
                response.append(String.format(BASE64_IMG_HTML, dataValues[idx]));
            }
        }
        
        return response.toString();
    }

}