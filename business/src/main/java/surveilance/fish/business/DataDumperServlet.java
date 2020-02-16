package surveilance.fish.business;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import surveilance.fish.business.base.BaseEncSecServlet;
import surveilance.fish.business.track.Tracker;
import surveilance.fish.security.AesEncrypter;
import surveilance.fish.security.AesUtil;
import surveilance.fish.security.RsaEncrypter;

public class DataDumperServlet extends BaseEncSecServlet {

    private static final long serialVersionUID = 2329853925310660049L;
    
    public DataDumperServlet(AesEncrypter aesEncrypter, RsaEncrypter rsaEncrypter, AesUtil aesUtil) {
        super(aesEncrypter, rsaEncrypter, aesUtil);
    }
    
    @Override
    protected void doGetSecured(HttpServletRequest request, HttpServletResponse response ) {
        //TODO: maybe add Apache httpcore dependency and use org.apache.http.entity.ContentType.APPLICATION_JSON.toString()
        response.setContentType("application/json; charset=utf-8");
        try {
            response.getWriter().println(getObjectMapper().writeValueAsString(createDataBrick(Tracker.getInstance().dumpData())));
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (IOException e) {
            System.out.println("Cannot send data brick");
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    
}
