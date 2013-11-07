/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ambimmort.dmc.client;

import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.proxy.Invoker;

/**
 *
 * @author Administrator
 */
public class DMCInvoker implements Invoker {

    private Class iInterface = null;
    private String implName = null;
    private String ip = null;
    private String domain = null;

    public DMCInvoker() {
    }

    public DMCInvoker(Class iInterface, String name, String ip, String domain) {
        this.iInterface = iInterface;
        this.implName = name;
        this.ip = ip;
        this.domain = domain;
    }

    public Object invoke(Object o, Method method, Object[] os) throws DomainUnAvailableException, RuntimeException, Throwable {
        try {
            String name = implName;
            String methodName = method.getName();
            JSONObject obj = new JSONObject();
            obj.put("returnType", method.getReturnType().getName());
            JSONArray types = new JSONArray();
            for (Class t : method.getParameterTypes()) {
                types.add(t.getName());
            }
            obj.put("parameterType", types);
            obj.put("name", methodName);
            JSONArray values = new JSONArray();
            if (os != null) {
                for (Object ot : os) {
                    values.add(ot.toString());
                }
            }
            obj.put("values", Base64.encodeBase64String(values.toString().getBytes()));
            WebConversation webConversation = new WebConversation();
            webConversation.set_connectTimeout(1);
            webConversation.set_readTimeout(30);
            int rand = (int) (Math.random() * 10000);
            PostMethodWebRequest post = new PostMethodWebRequest(ip + "/router/DMC/" + domain + "/DMCService?rand=" + rand + "&name=" + name + "&methodName=" + methodName, new ByteArrayInputStream(obj.toString().getBytes("utf-8")), "text/json");
            post.setParameter("name", name);
            post.setParameter("methodName", methodName);
            WebResponse wr = webConversation.getResponse(post);
            StringBuilder result = new StringBuilder();
            List<String> lines = IOUtils.readLines(wr.getInputStream());
            for (String line : lines) {
                result.append(line).append("\n");
            }
            result.deleteCharAt(result.length() - 1);

            if (method.getReturnType().getName().equals("void")) {
                return null;
            } else {
                String tmp = "TTT_DDD_EXCEPTION_$_$_$_$_SSDSSX_" + domain + "_" + rand + "_[";
                String resultStr = result.toString();
                if (("TTT_DDD_EXCEPTION_$_$_$_$_SSDSSX_" + domain + "_" + rand + "_[unavailable]").endsWith(resultStr)) {
                    throw new DomainUnAvailableException(domain);
                } else if (resultStr.startsWith(tmp)) {
                    throw new RuntimeException(resultStr.substring(tmp.length(), resultStr.length() - 2));
                } else {
                    return result.toString();
                }
            }
        } catch (Throwable t) {
            throw t;
        }

    }
}
