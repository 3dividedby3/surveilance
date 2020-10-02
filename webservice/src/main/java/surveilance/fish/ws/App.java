package surveilance.fish.ws;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import surveilance.fish.business.DataDumperServlet;
import surveilance.fish.business.UIServlet;
import surveilance.fish.business.comm.BeCommandServlet;
import surveilance.fish.business.proxy.MediatorServlet;
import surveilance.fish.business.security.AuthManageServlet;
import surveilance.fish.common.FixedSizeArrayDeque;
import surveilance.fish.security.AesDecrypter;
import surveilance.fish.security.AesEncrypter;
import surveilance.fish.security.AesUtil;
import surveilance.fish.security.RsaDecrypter;
import surveilance.fish.security.RsaEncrypter;

public class App {
    
    public static final int MAX_NO_IMG_SAVED = 3;
    public static final String ENCODED_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCJ7F0c19UaNelx4OkmpR/UebPENeQaYKKcbYmOEEh2xsWkM2CD3qfEUmXy2oNTkrs5dEeSDqQyCk4OAaB/vuTYuIAdkrM7IYLjvmkB4vfwtWxv07A8rIPSO0GXyzFDHgmmKDxYYCAnyY63IF37ReYk9OlG/JwUBDEtlU8yjaOjkQIDAQAB";

    //you must use the port you are given by Heroku in the PORT environment variable
    public static final int APP_PORT = Integer.valueOf(System.getenv("PORT"));

    public static void main(String[] args) throws Exception {
        Server server = new Server(APP_PORT);

        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);

        ServletHolder holderDataDumperServlet = new ServletHolder(new DataDumperServlet(new AesEncrypter()
                , new RsaEncrypter(ENCODED_PUBLIC_KEY, false)
                , new AesUtil()));
        
        ServletHolder holderUIServlet = new ServletHolder(new UIServlet(new AesDecrypter()
                , new RsaDecrypter(ENCODED_PUBLIC_KEY)
                , new FixedSizeArrayDeque<>(MAX_NO_IMG_SAVED)));
        
        ServletHolder authManageServlet = new ServletHolder(new AuthManageServlet(new AesDecrypter(), new RsaDecrypter(ENCODED_PUBLIC_KEY)));
        
        ServletHolder holderBeCommandServlet = new ServletHolder(new BeCommandServlet(new AesEncrypter()
                , new RsaEncrypter(ENCODED_PUBLIC_KEY, false)
                , new AesUtil()));
        
        ServletHolder holderMediatorServlet = new ServletHolder(new MediatorServlet());
        
        handler.addServletWithMapping(holderDataDumperServlet, "/dump");
        handler.addServletWithMapping(holderUIServlet, "/image");
        handler.addServletWithMapping(authManageServlet, "/auth");
        handler.addServletWithMapping(holderBeCommandServlet, "/command");
        handler.addServletWithMapping(holderMediatorServlet, "/proxy");
        
        server.start();
        server.join();
    }
}
