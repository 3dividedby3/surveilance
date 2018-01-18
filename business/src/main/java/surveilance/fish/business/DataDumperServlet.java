package surveilance.fish.business;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import surveilance.fish.business.track.Tracker;
import surveilance.fish.model.DataBrick;
import surveilance.fish.model.ViewerData;
import surveilance.fish.security.AesEncrypter;
import surveilance.fish.security.AesUtil;
import surveilance.fish.security.RsaEncrypter;

public class DataDumperServlet extends HttpServlet {

    private static final long serialVersionUID = 2329853925310660049L;

    private final RsaEncrypter rsaEncrypter;
    private final AesEncrypter aesEncrypter;
    private final AesUtil aesUtil;
    private final ObjectWriter objectWriter;
    
    public DataDumperServlet(AesEncrypter aesEncrypter, RsaEncrypter rsaEncrypter, AesUtil aesUtil) {
        this.aesEncrypter = aesEncrypter;
        this.rsaEncrypter = rsaEncrypter;
        this.aesUtil = aesUtil;
        objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response ) throws IOException  {
        Tracker.getInstance().trackUserData(request);
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(objectWriter.writeValueAsString(createDataBrick(Tracker.getInstance().dumpData())));
    }

    private DataBrick createDataBrick(List<ViewerData> data) throws IOException  {
        DataBrick dataBrick = new DataBrick();
        String dataAsString = objectWriter.writeValueAsString(data);
        byte[] key = aesUtil.createAesKey();
        dataBrick.setAesKey(rsaEncrypter.encryptAndEncode(key));
        dataBrick.setPayload(aesEncrypter.encryptAndEncode(dataAsString, key));
        
        return dataBrick;
    }
    
}