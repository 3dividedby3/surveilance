package surveilance.fish.ws;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

import surveilance.fish.business.UIServlet;

public class App {
    //you must use the port you are given by Heroku in the PORT environment variable
    public static final int APP_PORT = Integer.valueOf(System.getenv("PORT"));

    public static void main(String[] args) throws Exception {
        Server server = new Server(APP_PORT);

        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);

        handler.addServletWithMapping(UIServlet.class, "/*");
        
        server.start();
        server.join();
    }
}
