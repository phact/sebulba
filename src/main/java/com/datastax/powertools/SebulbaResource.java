package com.datastax.powertools;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
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
        UUID event_uuid = UUIDs.timeBased();
        String event_type = "register";

        this.racerName = racerName;

        PreparedStatement insertEvent = stmts.getInsertEvent();
        BoundStatement query = insertEvent.bind()
                .setString("id", racerName)
                .setUUID("event_uuid", event_uuid)
                .setString("event_type", event_type);
        session.execute(query);

        return service.confirmation(racerName);
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/stop/")
    public String stopRace() {
        UUID event_uuid = UUIDs.timeBased();
        UUID race_id = UUIDs.timeBased();
        String event_type = "stop";

        long endTime= new Date().getTime();

        PreparedStatement insertEvent = stmts.getInsertEvent();
        //"(id, racer_id , duration ,start_time, end_time , alt_avg , bat_avg , cam_avg , mode_avg , spd_avg , temp_height_avg , wifi_avg ) " +
        BoundStatement query = insertEvent.bind()
                .setString("id", racerName)
                .setUUID("event_uuid", event_uuid)
                .setString("event_type", event_type)
                .setLong("end_time", new Date().getTime());

        session.execute(query);

        PreparedStatement insertRaceSummary = stmts.getInsertRaceSummary();
        query = insertRaceSummary.bind()
                .setString("id", "race-"+race_id.toString())
                .setString("racer_id", racerName)
                .setLong("duration", endTime-startTime)
                .setLong("start_time", startTime)
                .setLong("end_time", endTime);
        //TODO: compute the averages

        session.execute(query);

        return service.confirmation(racerName);
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/fail/")
    public String failRace() {
        UUID event_uuid = UUIDs.timeBased();
        UUID race_id = UUIDs.timeBased();
        String event_type = "fail";

        long endTime= new Date().getTime();

        PreparedStatement insertEvent = stmts.getInsertEvent();
        //"(id, racer_id , duration ,start_time, end_time , alt_avg , bat_avg , cam_avg , mode_avg , spd_avg , temp_height_avg , wifi_avg ) " +
        BoundStatement query = insertEvent.bind()
                .setString("id", racerName)
                .setUUID("event_uuid", event_uuid)
                .setString("event_type", event_type)
                .setLong("end_time", new Date().getTime());

        session.execute(query);

        //TODO: Decide on whether we should have summaries for failed races. Maybe a new table or introduce race_type?
        /*
        PreparedStatement insertRaceSummary = stmts.getInsertRaceSummary();
        query = insertRaceSummary.bind()
                .setString("id", "race-"+race_id.toString())
                .setString("racer_id", racerName)
                .setLong("duration", 99999999)
                .setLong("start_time", startTime)
                .setLong("end_time", endTime);
        */
        //TODO: compute the averages

        session.execute(query);

        return service.confirmation(racerName);
    }


    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/start/")
    public String startRace() {
        UUID event_uuid = UUIDs.timeBased();
        String event_type = "start";

        startTime = new Date().getTime();

        PreparedStatement insertEvent = stmts.getInsertEvent();
        BoundStatement query = insertEvent.bind()
                .setString("id", racerName)
                .setUUID("event_uuid", event_uuid)
                .setString("event_type", event_type)
                .setLong("start_time", startTime);
        session.execute(query);

        return service.confirmation(racerName);
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/event/")
    public Event insertEvent(Event event) {
        UUID event_uuid = UUIDs.timeBased();
        String event_type = "payload";

        PreparedStatement insertEvent = stmts.getInsertEvent();
        //"(id, event_id, event_uuid, event_type, start_time, end_time, alt, bat, cam, mode, spd, temp_height, wifi) " +
        BoundStatement query = insertEvent.bind()
                .setString("id", racerName)
                .setLong("event_id", event.getEventId())
                .setUUID("event_uuid", event_uuid)
                .setString("event_type", event_type)
                .setLong("alt", event.getAlt())
                .setLong("bat", event.getBat())
                .setLong("cam", event.getCam())
                .setLong("mode", event.getMode())
                .setLong("spd", event.getSpd())
                .setLong("temp_height", event.getTempHeight())
                .setLong("wifi", event.getWifi());
        session.execute(query);

        return event;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/position/")
    public DronePosition insertPosition(DronePosition position) {
        UUID event_uuid = UUIDs.timeBased();
        String event_type = "payload";

        //"(mvo_vel_x,mvo_vel_y,mvo_vel_z,mvo.pos_x,mvo.pos_y,mvo_pos_z,imu_acc_x,imu_acc_y,imu_acc_z,
        // imu_gyro_x,imu_gyro_y,imu_gyro_z,imu_q0,imu_q1,imu_q2, imu_q3,imu_vg_x,imu_vg_y,imu_vg_z)" +
        PreparedStatement insertEvent = stmts.getInsertPosition();
        BoundStatement query = insertEvent.bind()
                .setString("id", racerName)
                .setLong("time", new Date().getTime())
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
        session.execute(query);

        return position;
    }


    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "hello";
    }
}
