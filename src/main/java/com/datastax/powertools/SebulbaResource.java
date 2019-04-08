package com.datastax.powertools;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.utils.UUIDs;
import com.datastax.driver.dse.DseSession;
import com.datastax.powertools.managed.DSEManager;
import com.datastax.powertools.managed.DSEStmts;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.net.ServerSocket;
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

    @Inject
    void setup() {
        dseManager.configure(config);
        dseManager.start();
        session = dseManager.getSession();
        stmts = dseManager.getStmts();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/event/{racerName}")
    public String insertEvent(@PathParam("racerName") String racerName) {
        UUID event_id = UUIDs.timeBased();
        String event_type = "register";

        PreparedStatement insertEvent = stmts.getInsertEvent();
        BoundStatement query = insertEvent.bind(racerName, event_id, event_type);
        session.execute(query);

        return service.confirmation(racerName);
    }


    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "hello";
    }
}