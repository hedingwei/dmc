/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ambimmort.dmc.client;

import org.apache.commons.proxy.ProxyFactory;

/**
 *
 * @author Administrator
 */
public class DMCBuilder<T> {

    private String ip = null;
    private ProxyFactory f = null;
    private Class iInterface = null;
    private String implClass = null;
    private String domain = null;

    public DMCBuilder(Class<T> iInterface, String ip, String implClass) {
        this(iInterface, ip, "default", implClass);
    }

    public DMCBuilder(Class<T> iInterface, String ip, String domain, String implClass) {
        this.ip = ip;
        this.iInterface = iInterface;
        f = new ProxyFactory();
        this.implClass = implClass;
        this.domain = domain;
    }

    public T build() {
        Object test = f.createInvokerProxy(new DMCInvoker(iInterface, this.implClass, this.ip, domain), new Class[]{iInterface});
        return (T) test;
    }

}
