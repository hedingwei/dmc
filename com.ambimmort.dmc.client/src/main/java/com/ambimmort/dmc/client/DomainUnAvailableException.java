/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ambimmort.dmc.client;

/**
 *
 * @author Administrator
 */
public class DomainUnAvailableException extends RuntimeException {

    private String domain = null;

    public DomainUnAvailableException(String domain) {
        this.domain = domain;
    }

    public String getDomain() {
        return domain;
    }

}
