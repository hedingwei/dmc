/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ambimmort.dmc.server.core;

/**
 *
 * @author Administrator
 */
public class MemoryEater {

    public String sayHello(String username) {
        String[][] dd = new String[10000000][10000];
        for (int i = 0; i < dd.length; i++) {
            for (int j = 0; j < dd[i].length; j++) {
                dd[i][j] = new String("ij"+i+"m"+j);
            }
        }
        return "dddd";
    }
    
    public String sayHelloFine(String username){
        return "ok";
    }
}
