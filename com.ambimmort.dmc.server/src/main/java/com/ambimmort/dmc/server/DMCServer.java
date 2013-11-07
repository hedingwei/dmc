package com.ambimmort.dmc.server;

import com.ambimmort.dmc.server.core.DMCService;
import com.ambimmort.dmc.server.core.DomainManager;
import com.ambimmort.dmc.server.core.ServletRouter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public final class DMCServer {

    private Server server;
    private ServletContextHandler context;

    private int port = -1;

    public int getPort() {
        return port;
    }

    public DMCServer(int port) {
        this("default_" + port, port);
    }

    public DMCServer(String domain, int port) {
        this.port = port;
        server = new Server(port);
        context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new Handler[]{context});
        server.setHandler(contexts);
        addServlet(ServletRouter.class, "/router/*");
        addServlet(DMCService.class, "/DMC/DMCService");
        DomainManager dm = new DomainManager();
        dm.setServer(this);
        dm.registerDomain(domain, port);
        context.setAttribute("domainManager", dm);
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
        } catch (Exception ex) {
            Logger.getLogger(DMCServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void stop() {
        try {
            server.stop();
        } catch (Exception ex) {
            Logger.getLogger(DMCServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        new DMCServer("test", 8999).start();
        new DMCServer("test1", 9900).start();
    }
}
