package com.datastax.powertools.managed;

/*
 *
 * @author Sebastián Estévez on 4/6/19.
 *
 */


import com.datastax.driver.core.exceptions.NoHostAvailableException;
import com.datastax.driver.dse.DseCluster;
import com.datastax.driver.dse.DseSession;
import com.datastax.driver.dse.graph.GraphOptions;
import com.datastax.powertools.SebulbaConfiguration;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DSEManager {
    private static final Logger LOG = Logger.getLogger(DSEManager.class);


    private int cqlPort;
    private String[] contactPoints;
    private DseCluster cluster;
    private DSEStmts.Prepared stmts;
    private String graphName;

    private DseSession session;
    private String username;
    private String password;
    private String keyspaceName;
    private String replicationStrategy;

    public DseSession getSession() {
        return session;
    }


    public void configure(SebulbaConfiguration config) {
        contactPoints = config.getContactPoints().split(",");
        cqlPort = config.getCqlPort();
        username = config.getCqlUserName();
        password = config.getCqlPassword();
        keyspaceName = config.getKeyspaceName();
        replicationStrategy = config.getReplicationStrategy();
        graphName = config.getGraphName();
    }

    public void start() {
        DseCluster.Builder builder = DseCluster.builder().
                addContactPoints(contactPoints).
                withPort(cqlPort).
                withCredentials(username, password).
                withGraphOptions(new GraphOptions().setGraphName(graphName)).
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

    public void setGraphName(String graphName) {
        this.graphName = graphName;
    }
}
