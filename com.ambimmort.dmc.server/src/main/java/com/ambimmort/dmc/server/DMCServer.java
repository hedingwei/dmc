package com.ambimmort.dmc.server;

import com.ambimmort.dmc.server.core.ClassService;
import com.ambimmort.dmc.server.core.DMCService;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Hello world!
 *
 */
public final class DMCServer {

    private Server server;
    private ServletContextHandler context;

    public DMCServer(int port) {
        server = new Server(port);
        context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");
        context.setClassLoader(DMCServer.class.getClassLoader());
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new Handler[]{context});
        server.setHandler(contexts);
        addServlet(DMCService.class, "/DMC/DMCService");
    }

    public ServletContextHandler getContext() {
        return context;
    }

    public void addServlet(Class<? extends HttpServlet> aClass, String url) {
        context.addServlet(new ServletHolder(aClass), url);
    }

    public void start() {
        try {
            server.start();
//            server.join();
        } catch (Exception ex) {
            Logger.getLogger(DMCServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        new DMCServer(8999).start();
    }
}
