package com.datastax.powertools;

import com.datastax.driver.core.*;
import com.datastax.driver.core.utils.UUIDs;
import com.datastax.driver.dse.DseSession;
import com.datastax.powertools.api.DronePosition;
import com.datastax.powertools.api.Event;
import com.datastax.powertools.managed.DSEManager;
import com.datastax.powertools.managed.DSEStmts;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.UUID;

@Path("/sebulba")
public class SebulbaResource {

    @Inject
    DSEManager dseManager;

    @Inject
    SebulbaService service;

    @Inject
    SebulbaConfiguration config;

    private DseSession session;
    private DSEStmts.Prepared stmts;

    @SessionScoped
    private String racerName = "unknown";

    @SessionScoped
    private long startTime;

    @SessionScoped
    private String currentFlight = "unknown";

    @Inject
    void setup() {
        dseManager.configure(config);
        dseManager.start();
        session = dseManager.getSession();
        stmts = dseManager.getStmts();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/setup/{racerId}")
    public String setupRace(@PathParam("racerId") String racerName) {
        UUID event_id = UUIDs.timeBased();
        String event_type = "register";
        long eventTime= new Date().getTime();
        currentFlight = "";
        BatchStatement batchStatement = new BatchStatement();
        this.racerName = racerName;

        PreparedStatement selectPerson = stmts.getSelectPerson();

        String name = racerName;
        PreparedStatement insertEvent;
        BoundStatement query;

        Row result = session.execute(selectPerson.bind().setString("attendee_id", racerName)).one();
        if (result != null)
        {
            name = result.getString("first_name");
        }
        else
        {
            insertEvent = stmts.getInsertPerson();
            query = insertEvent.bind()
                        .setString("attendee_id", racerName)
                        .setString("first_name", racerName);
            batchStatement.add(query);
        }

        insertEvent = stmts.getInsertEvent();
        query = insertEvent.bind()
                .setString("id", racerName)
                .setUUID("event_id", event_id)
                .setString("event_type", event_type)
                .setLong("event_time", eventTime);
        batchStatement.add(query);

        session.execute(batchStatement);

        return service.confirmation(racerName);
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/stop/")
    public String stopRace(@QueryParam("status") String status) {
        if (status == null || status.isEmpty())
        {
            status = "completed";
        }
        endRace(UUIDs.timeBased(), status);
        return service.confirmation(racerName);
    }

    private void endRace(UUID event_id, String event_type)
    {
        long endTime= new Date().getTime();
        BatchStatement batchStatement = new BatchStatement();

        PreparedStatement insertEvent = stmts.getInsertEvent();
        //"(id, racer_id , duration ,start_time, end_time , alt_avg , bat_avg , cam_avg , mode_avg , spd_avg , temp_height_avg , wifi_avg ) " +
        BoundStatement query = insertEvent.bind()
                .setString("id", racerName)
                .setUUID("event_id", event_id)
                .setString("event_type", event_type)
                .setLong("event_time", endTime);

        session.execute(query);

        PreparedStatement insertRaceSummary = stmts.getInsertRaceSummary();
        query = insertRaceSummary.bind()
                .setString("id", currentFlight)
                .setString("racer_id", racerName)
                .setLong("duration", endTime-startTime)
                .setLong("start_time", startTime)
                .setLong("end_time", endTime);
        //TODO: compute the averages

        batchStatement.add(query);

        insertEvent = stmts.getInsertFlight();
        query = insertEvent.bind()
                .setString("flight_id", currentFlight)
                .setString("racer_id", racerName)
                .setString("race_status", event_type)
                .setLong("end_time", endTime)
                .setLong("duration", endTime-startTime);
        batchStatement.add(query);

        insertEvent = stmts.getInsertFlightEvents();
        query = insertEvent.bind()
                .setString("flight_id", currentFlight)
                .setUUID("event_uuid", event_id)
                .setString("event_type", event_type)
                .setLong("start_time", startTime)
                .setLong("end_time", endTime);
        batchStatement.add(query);

        insertEvent = stmts.getInsertEventsIncludedInFlight();
        query = insertEvent.bind()
                .setString("flight_events_flight_id", currentFlight)
                .setLong("flight_events_start_time", startTime)
                .setUUID("flight_events_event_uuid", event_id)
                .setString("flight_flight_id", currentFlight);
        batchStatement.add(query);

        session.execute(batchStatement);

        currentFlight = "";
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/fail/")
    public String failRace() {
        endRace(UUIDs.timeBased(), "fail");
        return service.confirmation(racerName);
    }


    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/start/")
    public String startRace() {
        UUID event_id = UUIDs.timeBased();
        UUID race_id = UUIDs.timeBased();
        String event_type = "start";
        currentFlight = "race-"+race_id.toString();
        startTime = new Date().getTime();

        BatchStatement batchStatement = new BatchStatement();

        PreparedStatement insertEvent = stmts.getInsertEvent();
        BoundStatement query = insertEvent.bind()
                .setString("id", racerName)
                .setUUID("event_id", event_id)
                .setString("event_type", event_type)
                .setLong("event_time", startTime);
        batchStatement.add(query);

        insertEvent = stmts.getInsertFlight();
        query = insertEvent.bind()
                .setString("flight_id", currentFlight)
                .setString("racer_id", racerName)
                .setLong("start_time", startTime);
        batchStatement.add(query);

        insertEvent = stmts.getInsertFlightEvents();
        query = insertEvent.bind()
                .setString("flight_id", currentFlight)
                .setUUID("event_uuid", event_id)
                .setString("event_type", event_type)
                .setLong("start_time", startTime);
        batchStatement.add(query);

        insertEvent = stmts.getInsertPersonFlewInFlight();
        query = insertEvent.bind()
                .setString("flight_flight_id", currentFlight)
                .setString("person_attendee_id", racerName);
        batchStatement.add(query);

        insertEvent = stmts.getInsertEventsIncludedInFlight();
        query = insertEvent.bind()
                .setString("flight_events_flight_id", currentFlight)
                .setLong("flight_events_start_time", startTime)
                .setUUID("flight_events_event_uuid", event_id)
                .setString("flight_flight_id", currentFlight);
        batchStatement.add(query);

        session.execute(batchStatement);

        return service.confirmation(racerName);
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/event/")
    public Event insertEvent(Event event) {
        UUID event_id = UUIDs.timeBased();
        String event_type = "payload";

        BatchStatement batchStatement = new BatchStatement();

        PreparedStatement insertEvent = stmts.getInsertEvent();
        BoundStatement query = insertEvent.bind()
                .setString("id", racerName)
                .setUUID("event_id", event_id)
                .setString("event_type", event_type)
                .setLong("event_time", new Date().getTime())
                .setLong("alt", event.getAlt())
                .setLong("bat", event.getBat())
                .setLong("cam", event.getCam())
                .setLong("mode", event.getMode())
                .setLong("spd", event.getSpd())
                .setLong("temp_height", event.getTempHeight())
                .setLong("wifi", event.getWifi());
        batchStatement.add(query);

        insertEvent = stmts.getInsertFlightEvents();
        query = insertEvent.bind()
                .setString("flight_id", currentFlight)
                .setUUID("event_uuid", event_id)
                .setString("event_type", event_type)
                .setLong("start_time", startTime)
                .setLong("alt", event.getAlt())
                .setLong("bat", event.getBat())
                .setLong("cam", event.getCam())
                .setLong("mode", event.getMode())
                .setLong("spd", event.getSpd())
                .setLong("temp_height", event.getTempHeight())
                .setLong("wifi", event.getWifi());
        batchStatement.add(query);

        insertEvent = stmts.getInsertEventsIncludedInFlight();
        query = insertEvent.bind()
                .setString("flight_events_flight_id", currentFlight)
                .setLong("flight_events_start_time", startTime)
                .setUUID("flight_events_event_uuid", event_id)
                .setString("flight_flight_id", currentFlight);
        batchStatement.add(query);

        session.execute(batchStatement);

        return event;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/position/")
    public DronePosition insertPosition(DronePosition position) {
        long eventTime = new Date().getTime();
        BatchStatement batchStatement = new BatchStatement();

        //"(mvo_vel_x,mvo_vel_y,mvo_vel_z,mvo.pos_x,mvo.pos_y,mvo_pos_z,imu_acc_x,imu_acc_y,imu_acc_z,
        // imu_gyro_x,imu_gyro_y,imu_gyro_z,imu_q0,imu_q1,imu_q2, imu_q3,imu_vg_x,imu_vg_y,imu_vg_z)" +
        PreparedStatement insertEvent = stmts.getInsertPosition();
        BoundStatement query = insertEvent.bind()
                .setString("id", racerName)
                .setLong("time", eventTime)
                .setDouble("mvo_vel_x", position.getMvoVelX())
                .setDouble("mvo_vel_y", position.getMvoVelY())
                .setDouble("mvo_vel_z", position.getMvoVelZ())
                .setDouble("mvo_pos_x", position.getMvoPosX())
                .setDouble("mvo_pos_y", position.getMvoPosY())
                .setDouble("mvo_pos_z", position.getMvoPosZ())
                .setDouble("imu_acc_x", position.getImuAccX())
                .setDouble("imu_acc_y", position.getImuAccY())
                .setDouble("imu_acc_z", position.getImuAccZ())
                .setDouble("imu_gyro_x", position.getImuGyroX())
                .setDouble("imu_gyro_y", position.getImuGyroY())
                .setDouble("imu_gyro_z", position.getImuGyroZ())
                .setDouble("imu_q0", position.getImuQ0())
                .setDouble("imu_q1", position.getImuQ1())
                .setDouble("imu_q2", position.getImuQ2())
                .setDouble("imu_q3", position.getImuQ3())
                .setDouble("imu_vg_x", position.getImuVgX())
                .setDouble("imu_vg_y", position.getImuVgY())
                .setDouble("imu_vg_z", position.getImuVgZ());
        batchStatement.add(query);

        insertEvent = stmts.getInsertFlightTelematics();
        query = insertEvent.bind()
                .setString("flight_id", currentFlight)
                .setLong("time", eventTime)
                .setDouble("mvo_vel_x", position.getMvoVelX())
                .setDouble("mvo_vel_y", position.getMvoVelY())
                .setDouble("mvo_vel_z", position.getMvoVelZ())
                .setDouble("mvo_pos_x", position.getMvoPosX())
                .setDouble("mvo_pos_y", position.getMvoPosY())
                .setDouble("mvo_pos_z", position.getMvoPosZ())
                .setDouble("imu_acc_x", position.getImuAccX())
                .setDouble("imu_acc_y", position.getImuAccY())
                .setDouble("imu_acc_z", position.getImuAccZ())
                .setDouble("imu_gyro_x", position.getImuGyroX())
                .setDouble("imu_gyro_y", position.getImuGyroY())
                .setDouble("imu_gyro_z", position.getImuGyroZ())
                .setDouble("imu_q0", position.getImuQ0())
                .setDouble("imu_q1", position.getImuQ1())
                .setDouble("imu_q2", position.getImuQ2())
                .setDouble("imu_q3", position.getImuQ3())
                .setDouble("imu_vg_x", position.getImuVgX())
                .setDouble("imu_vg_y", position.getImuVgY())
                .setDouble("imu_vg_z", position.getImuVgZ());
        batchStatement.add(query);

        insertEvent = stmts.getInsertTelematicsIncludedInFlight();
        query = insertEvent.bind()
                .setString("flight_telematics_flight_id", currentFlight)
                .setLong("flight_telematics_time", eventTime)
                .setString("flight_flight_id", currentFlight);
        batchStatement.add(query);

        session.execute(batchStatement);

        return position;
    }


    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "hello";
    }
}
