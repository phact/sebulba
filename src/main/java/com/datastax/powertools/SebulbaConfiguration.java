package com.datastax.powertools;

/*
 *
 * @author Sebastián Estévez on 4/6/19.
 *
 */


import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class SebulbaConfiguration {

    @ConfigProperty(name = "dse.contactPoints", defaultValue = "localhost")
    private String contactPoints;
    @ConfigProperty(name = "dse.ports", defaultValue = "9042")
    private int cqlPort;
    @ConfigProperty(name = "dse.cqlUserName", defaultValue = "cassandra")
    private String cqlUserName;
    @ConfigProperty(name = "dse.cqlPassword", defaultValue = "cassandra")
    private String cqlPassword;
    @ConfigProperty(name = "dse.keyspaceName", defaultValue = "sebulba")
    private String keyspaceName;
    @ConfigProperty(name = "dse.replicationStrategy", defaultValue ="{'class': 'SimpleStrategy', 'replication_factor': 1 }")
    private String replicationStrategy;

    public String getContactPoints() {
        return contactPoints;
    }

    public int getCqlPort() {
        return cqlPort;
    }

    public String getCqlUserName() {
        return cqlUserName;
    }

    public String getCqlPassword() {
        return cqlPassword;
    }

    public String getKeyspaceName() {
        return keyspaceName;
    }

    public String getReplicationStrategy() {
        return replicationStrategy;
    }

}
