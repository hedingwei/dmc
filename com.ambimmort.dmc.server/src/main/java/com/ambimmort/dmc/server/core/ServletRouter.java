package com.ambimmort.dmc.server.core;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletRouter extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void processRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        try {
            DomainManager dm = (DomainManager) request.getServletContext().getAttribute("domainManager");
            String path = request.getRequestURI();
            String rand = request.getParameter("rand");
            if(rand==null) return;
            if (path.equals("/router/DMC/DMCService")) {
            } else if (path.matches("/router/DMC/.*/DMCService")) {
                int startPosition = path.indexOf("/DMC/") + 5;
                int endPosition = path.indexOf("/", startPosition);
                String domain = path.substring(startPosition, endPosition);
                if (domain.equals("") || domain.equals("default") || domain.equals(dm.getDomain())) {
                    request.getRequestDispatcher("/DMC/DMCService").forward(request, response);
                } else {
                    if (!dm.isDomainAvailable(domain)) {
                        response.getWriter().println("TTT_DDD_EXCEPTION_$_$_$_$_SSDSSX_" + domain+"_"+rand+"_[unavailable]");
                        response.getWriter().close();
                    } else {
                        if (!dm.isProxied(domain)) {
                            request.getRequestDispatcher("/DMC/DMCService").forward(request, response);
                        } else {
                            request.getRequestDispatcher(path).forward(request, response);
                        }
                    }

                }
            }
        } catch (Throwable t) {
        }
    }

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}
