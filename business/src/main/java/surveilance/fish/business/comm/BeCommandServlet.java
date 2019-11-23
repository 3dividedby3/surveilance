package surveilance.fish.business.comm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Base64.Decoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import surveilance.fish.business.base.BaseEncServlet;
import surveilance.fish.model.BeCommand;
import surveilance.fish.security.AesEncrypter;
import surveilance.fish.security.AesUtil;
import surveilance.fish.security.RsaEncrypter;

public class BeCommandServlet extends BaseEncServlet {
    
    private static final long serialVersionUID = -7984353514018683593L;

    public static final String REQ_PARAM_COMMAND = "command";
    
    public static final Decoder BASE64_URL_DECODER = Base64.getUrlDecoder();

    //keep one command and overwrite it
    private volatile BeCommand beCommand;
    
    public BeCommandServlet(AesEncrypter aesEncrypter, RsaEncrypter rsaEncrypter, AesUtil aesUtil) {
        super(aesEncrypter, rsaEncrypter, aesUtil);
    }

    //using get to put command because of convenience
    @Override
    protected void doGetSecured(HttpServletRequest request, HttpServletResponse response) {
        //data is not encrypted so it can easily be put using a browser
        String commandBase64Url = request.getParameter(REQ_PARAM_COMMAND);
        if (commandBase64Url == null) {
            doSendBeCommand(response);
        } else {
            doSaveBeCommand(commandBase64Url);
        }
    }
    
    @Override
    protected void track(HttpServletRequest request) {
        //skip tracking this, creates too much data
    }

    private void doSendBeCommand(HttpServletResponse response) {
        if (beCommand == null) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }
        
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        try {
            String dataBrickJson = getObjectMapper().writeValueAsString(createDataBrick(beCommand));
            response.getWriter().println(dataBrickJson);
            beCommand = null;
        } catch (IOException e) {
            System.out.println("Cannot send data brick");
            e.printStackTrace();
        }
    }

    private void doSaveBeCommand(String commandBase64Url) {
        try {
            String commandDecoded = new String(BASE64_URL_DECODER.decode(commandBase64Url.getBytes("UTF-8")));
            String[] splitCommand = commandDecoded.split("=");
            beCommand = new BeCommand(splitCommand[0], splitCommand[1]);
        } catch (UnsupportedEncodingException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Cannot decode command :" + commandBase64Url);
            e.printStackTrace();
        }
    }
}
