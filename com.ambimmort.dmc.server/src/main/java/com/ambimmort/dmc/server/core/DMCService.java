package com.ambimmort.dmc.server.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class DMCService extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    org.apache.log4j.Logger logger = org.apache.log4j.Logger
            .getLogger(DMCService.class);

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setCharacterEncoding("utf-8");
            response.setContentType("text/html;charset=UTF-8");
            response.setCharacterEncoding("utf-8");
            String name = request.getParameter("name");
            String methodName = request.getParameter("methodName");
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    request.getInputStream()), 1024);
            String tmp = null;
            StringBuilder sb = new StringBuilder();
            while ((tmp = br.readLine()) != null) {
                sb.append(tmp).append("\n");
            }
            br.close();
            Object rst = DMCInterpretor.getInstance().doDMCService(name, methodName, sb.toString(), null);
            PrintWriter out = response.getWriter();
            out.print(rst);
            out.close();
        } catch (Throwable t) {
//            t.printStackTrace();
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
