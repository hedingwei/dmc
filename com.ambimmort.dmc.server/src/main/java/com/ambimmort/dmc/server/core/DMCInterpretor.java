/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ambimmort.dmc.server.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import javax.servlet.http.*;

/**
 *
 * @author Administrator
 */
public class DMCInterpretor {

    private static DMCInterpretor instance = null;

    public String doDMCClassService(String className) {

        try {
            Class cl = Class.forName(className);
            if (cl == null) {
                return null;
            }
            JSONObject obj = new JSONObject();
            obj.put("className", cl.getName());
            JSONObject methods = new JSONObject();
            for (Method m : cl.getMethods()) {
                JSONObject mo = new JSONObject();
                mo.put("returnType", m.getReturnType().getName());
                JSONArray ja = new JSONArray();
                for (Class pc : m.getParameterTypes()) {
                    ja.add(pc.getName());
                }
                mo.put("parameterType", ja);
                methods.put(m.getName(), mo);
            }
            obj.put("methods", methods);
            return obj.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public List<String> getAllClasses(File file) throws FileNotFoundException, IOException {
        List<String> classNames = new ArrayList<String>();
        ZipInputStream zip = new ZipInputStream(new FileInputStream(file));
        for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
            if (entry.getName().endsWith(".class") && !entry.isDirectory()) {
                // This ZipEntry represents a class. Now, what class does it represent?
                StringBuilder className = new StringBuilder();
                for (String part : entry.getName().split("/")) {
                    if (className.length() != 0) {
                        className.append(".");
                    }
                    className.append(part);
                    if (part.endsWith(".class")) {
                        className.setLength(className.length() - ".class".length());
                    }
                }
                classNames.add(className.toString());
            }
        }
        return classNames;
    }

    public Object doDMCService(String name, String methodName, String content, HttpSession session) throws Throwable {
        if (content == null) {
            return null;
        }
        //content = "{\"returnType\":\"java.lang.String\",\"parameterType\":[\"java.lang.String\"],\"values\":\"WyIvZXRjL3dlYm9zL3JlZ2lzdHJ5L2ljb25NYXAuaW5pIl0=\",\"name\":\"mapWith\"}";
        JSONObject obj = null;
        try {
            obj = JSONObject.fromObject(content);
        } catch (Throwable e) {
            throw e;
        }
        String v = new String(Base64.decodeBase64(obj.getString("values")));
        try {

            Class cl = Class.forName(name);
            if (cl == null) {
                return null;
            }
            JSONArray array = obj.getJSONArray("parameterType");
            JSONArray objs = JSONArray.fromObject(v);
            Class[] pcs = new Class[array.size()];
            Object[] values = new Object[objs.size()];
            for (int i = 0; i < array.size(); i++) {
                pcs[i] = Class.forName(array.getString(i));
            }
            for (int i = 0; i < objs.size(); i++) {
                values[i] = objs.get(i);
            }

            try {
                Object to = cl.newInstance();
                Method m = to.getClass().getDeclaredMethod(methodName, pcs);
                m.setAccessible(true);
                Object rt = m.invoke(to, values);
                return rt;
            } catch (Throwable er) {
                throw er;
            } finally {
//                    Thread.currentThread().setContextClassLoader(oriclassload);
            }

        } catch (SecurityException ex) {
            throw ex;
        } catch (ClassNotFoundException ex) {
            throw ex;
        }
    }

    public static DMCInterpretor getInstance() {
        if (instance == null) {
            instance = new DMCInterpretor();
        }
        return instance;
    }

    public DMCInterpretor() {
    }
}
