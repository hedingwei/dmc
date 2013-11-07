/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ambimmort.dmc.server.core;

import com.ambimmort.dmc.server.DMCServer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.ProxyServlet;

/**
 *
 * @author Administrator
 */
public class DomainManager {

    private String domainRoot = "";

    private String domain = "";

    private int port = -1;

    private int pid = -1;

    private FileLock domainLock = null;

    private DMCServer server = null;

    private HashMap<String, String> proxies = new HashMap<String, String>();

    public String getDomain() {
        return domain;
    }

    private boolean isWindowOS() {
        return OsCheck.OSType.Windows == OsCheck.getOperatingSystemType();
    }

    private boolean isLinuxOS() {
        return OsCheck.OSType.Linux == OsCheck.getOperatingSystemType();
    }

    public FileLock getDomainLock() {
        return domainLock;
    }

    public void setServer(DMCServer server) {
        this.server = server;
        this.port = server.getPort();
    }

    public String getProxy(String domain) {
        return proxies.get(domain);
    }

    public boolean isProxied(String domain) {

        if (this.domain.equals(domain)) {
            return false;
        }

        String key = "/router/DMC/" + domain + "/DMCService";

        if (proxies.containsKey(key) && (!proxies.get(key).equals("-1"))) {
            return true;
        } else {

            File f = new File(this.domainRoot + File.separator + domain + ".domain");
            if (!f.exists()) {
                proxies.put(key, "-1");
                return false;
            } else {
                FileLock fl = null;
                try {
                    fl = new FileOutputStream(this.domainRoot + File.separator + "locks" + File.separator + domain + ".lock").getChannel().tryLock();
                    if (fl == null) {
                        Properties p = new Properties();
                        p.load(new FileInputStream(f));
                        if (p.containsKey("port") && p.contains("domain")) {
                            if (domain.equals(p.getProperty("domain"))) {

                                proxies.put(key, "http://localhost:" + p.getProperty("port") + "/router/DMC/" + domain + "/DMCService");
                                ServletHolder proxy = new ServletHolder(ProxyServlet.Transparent.class);
                                System.out.println(proxy);
                                proxy.setEnabled(true);
                                proxy.setInitParameter("ProxyTo", proxies.get(key));
                                proxy.setInitParameter("Prefix", key);
                                proxy.setAsyncSupported(true);
                                server.getContext().addServlet(proxy, "/router/DMC/" + domain + "/DMCService");
                                return true;
                            }
                        }
                    } else {

                        fl.release();
                        proxies.put(key, "-1");
                        return false;
                    }

                } catch (Exception ex) {
                    try {
                        Properties p = new Properties();
                        p.load(new FileInputStream(f));
                        if (p.containsKey("port") && p.containsKey("domain")) {
                            if (domain.equals(p.getProperty("domain"))) {
                                proxies.put(key, "http://localhost:" + p.getProperty("port") + "/router/DMC/" + domain + "/DMCService");
                                ServletHolder proxy = new ServletHolder(ProxyServlet.Transparent.class);
                                proxy.setEnabled(true);
                                proxy.setInitParameter("ProxyTo", proxies.get(key));
                                proxy.setInitParameter("Prefix", key);
                                proxy.setAsyncSupported(true);
                                proxy.setInitOrder(Integer.parseInt(p.getProperty("port")));
                                server.getContext().addServlet(proxy, "/router/DMC/" + domain + "/DMCService");
                                return true;
                            }
                        }
                        proxies.put(key, "-1");
                        return false;
                    } catch (Exception ex1) {
                        Logger.getLogger(DomainManager.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                } finally {
                    if (fl != null) {
                        try {
                            fl.release();
                        } catch (IOException ex) {
                            Logger.getLogger(DomainManager.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }

            }

        }

        return false;
    }

    public boolean isDomainAvailable(String domain) {
        File f = new File(this.domainRoot + File.separator + domain + ".domain");
        if (f.exists()) {
            FileLock lock = null;
            try {
                lock = new FileOutputStream(this.domainRoot + File.separator + "locks" + File.separator + domain + ".lock").getChannel().tryLock();
                if (lock == null) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception ex) {
                return true;
            }
        }else{
            return false;
        }

    }

    public DomainManager() {
        if (isWindowOS()) {
            String usrHome = System.getProperty("user.home");
            domainRoot = usrHome + "/runtime/dmc/domains";
        } else if (isLinuxOS()) {
            domainRoot = "/opt/runtime/dmc/domains";
        }
        File domainRootFile = new File(domainRoot);
        File domainLocksDir = new File(this.domainRoot + File.separator + "locks");
        if (domainRootFile.exists()) {
            if (domainRootFile.isFile()) {
                System.out.println("domainRoot:" + this.domainRoot + " is a file. Program shutdown.");
                System.exit(-1);
            }
        } else {
            boolean flag = domainRootFile.mkdirs();
            if (flag == false) {
                System.out.println("domainRoot:" + this.domainRoot + " dir creation failed. Program shutdown.");
                System.exit(-1);
            }
        }

        if (domainLocksDir.exists()) {
            if (domainLocksDir.isFile()) {
                System.out.println("domain lock directory:" + domainLocksDir.getAbsolutePath() + " is a file. Program shutdown.");
                System.exit(-1);
            }
        } else {
            boolean flag = domainLocksDir.mkdirs();
            if (flag == false) {
                System.out.println("domain lock directory:" + domainLocksDir.getAbsolutePath() + " dir creation failed. Program shutdown.");
                System.exit(-1);
            }
        }
        System.out.println("domainRoot:" + domainRoot);
        this.pid = processedPid();

    }

    private int processedPid() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        String name = runtime.getName(); // format: "pid@hostname"  
        try {
            return Integer.parseInt(name.substring(0, name.indexOf('@')));
        } catch (Exception e) {
            return -1;
        }
    }

    public int getPid() {
        return pid;
    }

    public void registerDomain(String domain, int port) throws RuntimeException {
        this.domain = domain;
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            public void run() {
                if (getDomainLock() != null) {
                    try {
                        getDomainLock().release();
                    } catch (IOException ex) {
                        Logger.getLogger(DomainManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        }));
        if (check()) {

        } else {
            System.exit(-1);
        }

    }

    private boolean check() {

        FileLock lock = null;
        try {
            lock = new FileOutputStream(this.domainRoot + File.separator + "locks" + File.separator + this.domain + ".lock").getChannel().tryLock();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DomainManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DomainManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (lock != null) {
            this.domainLock = lock;
        }

        if (this.domainLock == null) {
            return false;
        } else {
            try {
                PrintWriter pw = new PrintWriter(this.domainRoot + File.separator + this.domain + ".domain");
                pw.println("domain=" + this.domain);
                pw.println("port=" + this.port);
                pw.println("pid=" + this.pid);
                pw.flush();
                pw.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(DomainManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            return true;
        }
    }

    public static void main(String[] args) throws FileNotFoundException, Exception {
//        FileLock lock = new FileOutputStream("d:/lock.lock").getChannel().tryLock();
//        System.out.println("locked=" + (lock == null) + "\t");
//        System.out.println(DomainManager.getInstance().processedPid());
        Thread.sleep(100000);
//        lock.release();

    }

}
