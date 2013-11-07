package com.ambimmort.dmc.server.core;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DMCService extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        DomainManager dm = (DomainManager) request.getServletContext().getAttribute("domainManager");
        String rand = request.getParameter("rand");
        if (rand == null) {
            return;
        }
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
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintWriter pw = new PrintWriter(baos);
            t.printStackTrace(pw);
            pw.close();
            response.getWriter().println("TTT_DDD_EXCEPTION_$_$_$_$_SSDSSX_" + dm.getDomain() + "_" + rand+"_["+baos.toString("utf-8")+"]");
            response.getWriter().close();
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
