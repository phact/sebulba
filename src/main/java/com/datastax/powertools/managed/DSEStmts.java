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
        private PreparedStatement insertPosition;
        private PreparedStatement insertRaceSummary;
        private PreparedStatement insertFlight;
        private PreparedStatement insertFlightEvents;
        private PreparedStatement insertFlightTelematics;
        private PreparedStatement insertPersonFlewInFlight;
        private PreparedStatement insertEventsIncludedInFlight;
        private PreparedStatement insertTelematicsIncludedInFlight;

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
            String statement = "INSERT INTO sebulba.events_by_racer " +
                    "(id, event_id, event_type, event_time, alt, bat, cam, mode, spd, temp_height, wifi) " +
                    "VALUES " +
                    "(:id, :event_id, :event_type, :event_time, :alt, :bat, :cam, :mode, :spd, :temp_height, :wifi)";
            insertEvent = prepare(statement);
            statement = "INSERT INTO sebulba.position_by_racer " +
                    "(id, time, mvo_vel_x,mvo_vel_y,mvo_vel_z,mvo_pos_x,mvo_pos_y,mvo_pos_z,imu_acc_x,imu_acc_y,imu_acc_z,imu_gyro_x,imu_gyro_y,imu_gyro_z,imu_q0,imu_q1,imu_q2,imu_q3,imu_vg_x,imu_vg_y,imu_vg_z)" +
                    "VALUES" +
                    "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            insertPosition = prepare(statement);
            statement = "INSERT INTO sebulba.races_by_duration " +
                    "(id, racer_id , duration ,start_time, end_time , alt_avg , bat_avg , cam_avg , mode_avg , spd_avg , temp_height_avg , wifi_avg ) " +
                    "VALUES" +
                    "(?,?,?,?,?,?,?,?,?,?,?,?)";
            insertRaceSummary = prepare(statement);

            statement = "INSERT INTO sebulba.flight " +
                    "(flight_id, duration, end_time, race_status, racer_id, start_time)" +
                    "VALUES " +
                    "(:flight_id, :duration, :end_time, :race_status, :racer_id, :start_time)";
            insertFlight = prepare(statement);

            statement = "INSERT INTO sebulba.flight_events " +
                        "(flight_id, start_time, event_uuid, alt, bat, cam, end_time, event_type, mode, race_status, spd, temp_height, wifi)" +
                        "VALUES " +
                        "(:flight_id, :start_time, :event_uuid, :alt, :bat, :cam, :end_time, :event_type, :mode, :race_status, :spd, :temp_height, :wifi)";
            insertFlightEvents = prepare(statement);

            statement = "INSERT INTO sebulba.flight_telematics" +
                        "(flight_id, time, imu_acc_x, imu_acc_y, imu_acc_z, imu_gyro_x, imu_gyro_y, imu_gyro_z, imu_q0, imu_q1, imu_q2, imu_q3, imu_vg_x, imu_vg_y, imu_vg_z, mvo_pos_x, mvo_pos_y, mvo_pos_z, mvo_vel_x, mvo_vel_y, mvo_vel_z)" +
                        "VALUES " +
                        "(:flight_id, :time, :imu_acc_x, :imu_acc_y, :imu_acc_z, :imu_gyro_x, :imu_gyro_y, :imu_gyro_z, :imu_q0, :imu_q1, :imu_q2, :imu_q3, :imu_vg_x, :imu_vg_y, :imu_vg_z, :mvo_pos_x, :mvo_pos_y, :mvo_pos_z, :mvo_vel_x, :mvo_vel_y, :mvo_vel_z)";
            insertFlightTelematics = prepare(statement);

            statement = "INSERT INTO sebulba.person__flew_in__flight" +
                        "(person_attendee_id, flight_flight_id)" +
                        "VALUES " +
                        "(:person_attendee_id, :flight_flight_id)";
            insertPersonFlewInFlight = prepare(statement);

            statement = "INSERT INTO sebulba.flight_events__included_in__flight" +
                        "(flight_events_flight_id, flight_events_start_time, flight_events_event_uuid, flight_flight_id)" +
                        "VALUES " +
                        "(:flight_events_flight_id, :flight_events_start_time, :flight_events_event_uuid, :flight_flight_id)";
            insertEventsIncludedInFlight = prepare(statement);

            statement = "INSERT INTO sebulba.flight_telematics__included_in__flight" +
                        "(flight_telematics_flight_id, flight_telematics_time, flight_flight_id)" +
                        "VALUES " +
                        "(:flight_telematics_flight_id, :flight_telematics_time, :flight_flight_id)";
            insertTelematicsIncludedInFlight = prepare(statement);
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
        public PreparedStatement getInsertPosition() {
            return insertPosition;
        }
        public PreparedStatement getInsertRaceSummary() {
            return insertRaceSummary;
        }

        public PreparedStatement getInsertFlight()
        {
            return insertFlight;
        }

        public PreparedStatement getInsertFlightEvents()
        {
            return insertFlightEvents;
        }

        public PreparedStatement getInsertFlightTelematics()
        {
            return insertFlightTelematics;
        }

        public PreparedStatement getInsertPersonFlewInFlight()
        {
            return insertPersonFlewInFlight;
        }

        public PreparedStatement getInsertEventsIncludedInFlight()
        {
            return insertEventsIncludedInFlight;
        }

        public PreparedStatement getInsertTelematicsIncludedInFlight()
        {
            return insertTelematicsIncludedInFlight;
        }
    }
}
