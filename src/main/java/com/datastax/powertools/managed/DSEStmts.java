package com.datastax.powertools.managed;

/*
 *
 * @author Sebastián Estévez on 4/6/19.
 *
 */

import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;

public class DSEStmts {


    private static final String KEYSPACE_PATTERN = ";;;KEYSPACE;;;";
    private static final String REPLICATION_STRATEGY_PATTERN = ";;;REPLICATION_STRATEGY;;;";

    private static String STMT_create_keyspace = String.format("CREATE KEYSPACE IF NOT EXISTS %s WITH REPLICATION = %s ;", KEYSPACE_PATTERN, REPLICATION_STRATEGY_PATTERN);

    public static class Prepared {
        private final String keyspace;
        private final Session session;
        private final String replicationStrategy;

        final PreparedStatement create_keyspace;
        private PreparedStatement insertEvent;

        public Prepared(Session session, String keyspace, String replicationStrategy) {
            this.keyspace = keyspace;
            this.session = session;
            this.replicationStrategy = replicationStrategy;

            create_keyspace = prepare(STMT_create_keyspace);

            //Ensure the keyspaceName exists
            session.execute(create_keyspace.bind());

            prepareStatements();
        }

        private void prepareStatements() {
            String statement = "INSERT INTO sebulba.events_by_racer (id, event_id, event_type) VALUES (?, ?, ?)";
            insertEvent = prepare(statement);
        }

        public PreparedStatement prepare(String stmt) {
            String withKeyspace
                    = stmt.replaceAll(KEYSPACE_PATTERN, this.keyspace);
            String withReplication
                    = withKeyspace.replaceAll(REPLICATION_STRATEGY_PATTERN, this.replicationStrategy);

            PreparedStatement prepared = session.prepare(withReplication);

            if (stmt.contains("solr_query")) {
                prepared.setConsistencyLevel(ConsistencyLevel.LOCAL_ONE);
            } else {
                prepared.setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM);
            }
            return prepared;
        }

        public PreparedStatement getInsertEvent() {
            return insertEvent;
        }
    }
}
