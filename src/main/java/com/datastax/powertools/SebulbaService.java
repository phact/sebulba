package com.datastax.powertools;

/*
 *
 * @author Sebastián Estévez on 4/6/19.
 *
 */


import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SebulbaService {
    public String confirmation(String name) {
        return "event saved for: " + name;
    }

}

