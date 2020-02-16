package surveilance.fish.business.track;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import surveilance.fish.model.ViewerData;

public class Tracker {
    
    private List<ViewerData> tracked;
    
    private static final Tracker INSTANCE = new Tracker();

    private Tracker() {
        tracked = Collections.synchronizedList(new ArrayList<>());
    }

    public static Tracker getInstance() {
        return INSTANCE;
    }

    public ViewerData trackUserData(HttpServletRequest request, String body) {
        Map<String, List<String>> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()) {
            String currentHeaderName = headerNames.nextElement();
            headers.put(currentHeaderName, Collections.list(request.getHeaders(currentHeaderName)));
        }
        
        String fullUrl = request.getRequestURL() + "?" + request.getQueryString();
        
        ViewerData viewerData = new ViewerData(headers, fullUrl, body);
        System.out.println(viewerData.getTimestamp() + " - Page accessed by: " + viewerData.getHeaders());
        tracked.add(viewerData);
        
        return viewerData;
    }

    public List<ViewerData> dumpData() {
        synchronized (tracked) {
            List<ViewerData> toReturn = new ArrayList<>(tracked);
            //TODO: should only clear the data if the client says so
            tracked.clear();

            return toReturn;
        }
    }
    
}
