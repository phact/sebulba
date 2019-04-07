package com.datastax.powertools.managed;

/*
 *
 * @author Sebastián Estévez on 4/6/19.
 *
 */


import com.datastax.driver.core.exceptions.NoHostAvailableException;
import com.datastax.driver.dse.DseCluster;
import com.datastax.driver.dse.DseSession;
import com.datastax.powertools.SebulbaConfiguration;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DSEManager {
    private static final Logger LOG = Logger.getLogger(DSEManager.class);


    private int cqlPort = 9042;
    private String[] contactPoints = new String[]{"localhost"};
    private DseCluster cluster;
    private DSEStmts.Prepared stmts;

    public DseSession getSession() {
        return session;
    }

    private DseSession session;
    private String username;
    private String password;
    private String keyspaceName;
    private String replicationStrategy;

    public void configure(SebulbaConfiguration config) {
        contactPoints = config.getContactPoints();
        cqlPort = config.getCqlPort();
        username = config.getCqlUserName();
        password = config.getCqlPassword();
        keyspaceName = config.getKeyspaceName();
        replicationStrategy= config.getReplicationStrategy();
    }

    public void start() {
        DseCluster.Builder builder = DseCluster.builder().
                addContactPoints(contactPoints).
                withPort(cqlPort).
                withCredentials(username, password).
                withoutJMXReporting();

        password = null; // defensive

        cluster = builder.build();
        try {
            session = this.cluster.connect();
            stmts = new DSEStmts.Prepared(session, keyspaceName, replicationStrategy);
        }catch (NoHostAvailableException e){
            LOG.error("DSE is not up.");
            System.exit(-1);
        }
    }

    public DSEStmts.Prepared getStmts() {
        return stmts;
    }
}
