package com.datastax.powertools;

/*
 *
 * @author Sebastián Estévez on 4/6/19.
 *
 */


public class SebulbaConfiguration {

    private String[] contactPoints;
    private int cqlPort;
    private String cqlUserName = "cassandra";
    private String cqlPassword = "cassandra";
    private String keyspaceName;
    private String replicationStrategy;

    public SebulbaConfiguration(String[] contactPoints, int cqlPort, String cqlUserName, String cqlPassword, String keyspaceName, String replicationStrategy) {
        this.contactPoints = contactPoints;
        this.cqlPort = cqlPort;
        if (cqlUserName != null) {
            this.cqlUserName = cqlUserName;
        }
        if (cqlPassword!= null) {
            this.cqlPassword = cqlPassword;
        }
        this.keyspaceName = keyspaceName;
        this.replicationStrategy = replicationStrategy;
    }

    public String[] getContactPoints() {
        return contactPoints;
    }

    public void setContactPoints(String[] contactPoints) {
        this.contactPoints = contactPoints;
    }

    public int getCqlPort() {
        return cqlPort;
    }

    public void setCqlPort(int cqlPort) {
        this.cqlPort = cqlPort;
    }

    public String getCqlUserName() {
        return cqlUserName;
    }

    public void setCqlUserName(String cqlUserName) {
        this.cqlUserName = cqlUserName;
    }

    public String getCqlPassword() {
        return cqlPassword;
    }

    public void setCqlPassword(String cqlPassword) {
        this.cqlPassword = cqlPassword;
    }

    public String getKeyspaceName() {
        return keyspaceName;
    }

    public void setKeyspaceName(String keyspaceName) {
        this.keyspaceName = keyspaceName;
    }

    public String getReplicationStrategy() {
        return replicationStrategy;
    }

    public void setReplicationStrategy(String replicationStrategy) {
        this.replicationStrategy = replicationStrategy;
    }
}
