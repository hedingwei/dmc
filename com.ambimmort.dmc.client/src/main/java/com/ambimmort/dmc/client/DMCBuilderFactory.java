/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ambimmort.dmc.client;

/**
 *
 * @author Administrator
 */
public class DMCBuilderFactory<T> {

    private String ip;
    private Class<T> anInterface = null;

    public DMCBuilderFactory(String ip, Class<T> anInterface) {
        this.anInterface = anInterface;
        this.ip = ip;
    }

    public DMCBuilder<T> makeBuilder(String implClass) {

        return new DMCBuilder<T>(anInterface, ip, implClass);
    }
}
