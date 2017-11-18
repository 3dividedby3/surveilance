package surveilance.fish.ws;

import java.io.IOException;
import java.net.InetSocketAddress;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

import surveilance.fish.business.UIServlet;

public class App {
    public static final int APP_PORT = 6900;

    public static void main(String[] args) throws Exception {
        Server server = new Server(APP_PORT);

        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);

        handler.addServletWithMapping(UIServlet.class, "/*");
        
        server.start();
        server.join();
    }
}
