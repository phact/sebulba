package com.datastax.powertools;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.utils.UUIDs;
import com.datastax.driver.dse.DseSession;
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
        String event_type = "stop";

        PreparedStatement insertEvent = stmts.getInsertEvent();
        BoundStatement query = insertEvent.bind()
                .setString("id", racerName)
                .setUUID("event_uuid", event_uuid)
                .setString("event_type", event_type)
                .setLong("end_time", new Date().getTime());
        session.execute(query);

        return service.confirmation(racerName);
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/start/")
    public String startRace() {
        UUID event_uuid = UUIDs.timeBased();
        String event_type = "start";

        PreparedStatement insertEvent = stmts.getInsertEvent();
        BoundStatement query = insertEvent.bind()
                .setString("id", racerName)
                .setUUID("event_uuid", event_uuid)
                .setString("event_type", event_type)
                .setLong("start_time", new Date().getTime());
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


    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "hello";
    }
}