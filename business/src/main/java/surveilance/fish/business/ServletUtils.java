package surveilance.fish.business;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

public class ServletUtils {

    public String readBody(HttpServletRequest request) throws IOException {
        String body = request.getReader().lines().collect(Collectors.joining());

        return body;
    }
}
