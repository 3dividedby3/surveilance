package surveilance.fish.business;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UIServlet extends HttpServlet {

    private static final long serialVersionUID = -6565586545385873380L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response ) throws ServletException,  IOException  {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("<h1>The UI is working!<br> Now the rest...</h1>");
    }
}