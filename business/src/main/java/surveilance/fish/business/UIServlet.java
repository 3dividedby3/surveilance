package surveilance.fish.business;

import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import surveilance.fish.model.DataBrick;

public class UIServlet extends HttpServlet {

    public static final int MAX_NO_IMG_SAVED = 3;
    public static final String BASE64_IMG_HTML = "<br><img src=\"data:image/png;base64, %s\"/>";

    private static final String ENCODED_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCJ7F0c19UaNelx4OkmpR/UebPENeQaYKKcbYmOEEh2xsWkM2CD3qfEUmXy2oNTkrs5dEeSDqQyCk4OAaB/vuTYuIAdkrM7IYLjvmkB4vfwtWxv07A8rIPSO0GXyzFDHgmmKDxYYCAnyY63IF37ReYk9OlG/JwUBDEtlU8yjaOjkQIDAQAB";

    private static final long serialVersionUID = -6565586545385873380L;

    private final AesDecrypter aesDecrypter;
    private final RsaDecrypter rsaDecrypter;
    private final ObjectMapper objectMapper;
    private final Map<Long, String> dataToDisplay;
    
    public UIServlet() {
        aesDecrypter = new AesDecrypter();
        rsaDecrypter = new RsaDecrypter(ENCODED_PUBLIC_KEY);
        objectMapper = new ObjectMapper();
        dataToDisplay = new LinkedHashMap<Long, String>(){
            private static final long serialVersionUID = 165645754658L;
            @Override
            protected boolean removeEldestEntry(Map.Entry<Long, String> eldest) {
                boolean result = MAX_NO_IMG_SAVED < size();
                if (result) {
                    System.out.println("Removing old data from data to display, timestamp [" + eldest.getKey() + "], value: [" + eldest.getValue() + "]");
                }
                return result;
            }
        };
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response ) throws IOException  {
        logRequestData(request);
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        response.getWriter().println("<h1>Check the images below</h1>");
        response.getWriter().println("<br>Data: " + getDataToDisplay());
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logRequestData(request);
        String body = null;
        try {
            body = request.getReader().lines().collect(Collectors.joining());
        } catch(IOException e) {
            System.out.println("Error while reading body of put request: " + e.getMessage());
        }
        System.out.println("Received data: " + body);
        if (body == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        DataBrick dataBrick = objectMapper.readValue(body, DataBrick.class);
        byte[] aesKey = rsaDecrypter.decrypt(dataBrick.getAesKey().getBytes());
        String data = new String(aesDecrypter.decrypt(dataBrick.getPayload(), aesKey));
        if (data != null) {
            System.out.println("Addind data to be displayed: " +data);
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
            for (String currentData : dataToDisplay.values()) {
                response.append(String.format(BASE64_IMG_HTML, currentData));
            }
        }
        
        return response.toString();
    }
    

    private void logRequestData(HttpServletRequest request) {
        String unixTime = String.valueOf(System.currentTimeMillis());
        StringBuilder builder = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()) {
            String currentHeader = headerNames.nextElement();
            builder.append(" | " + currentHeader + "=" + request.getHeader(currentHeader));
            
        }
        System.out.println(unixTime + " - Page accessed by: " + builder);
        
    }
}