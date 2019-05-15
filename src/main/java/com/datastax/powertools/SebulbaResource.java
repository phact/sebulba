package com.datastax.powertools;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.utils.UUIDs;
import com.datastax.driver.dse.DseSession;
import com.datastax.driver.dse.graph.GraphNode;
import com.datastax.driver.dse.graph.GraphResultSet;
import com.datastax.driver.dse.graph.GraphStatement;
import com.datastax.dse.graph.api.DseGraph;
import com.datastax.powertools.api.DronePosition;
import com.datastax.powertools.api.Event;
import com.datastax.powertools.api.GraphRepresentation;
import com.datastax.powertools.managed.DSEManager;
import com.datastax.powertools.managed.DSEStmts;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

import static org.apache.tinkerpop.gremlin.process.traversal.Order.decr;

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

        this.racerName = racerName;

        PreparedStatement insertEvent = stmts.getInsertEvent();
        BoundStatement query = insertEvent.bind()
                .setString("id", racerName)
                .setUUID("event_id", event_id)
                .setString("event_type", event_type)
                .setLong("event_time", eventTime);
        session.execute(query);

        return service.confirmation(racerName);
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/stop/")
    public String stopRace() {
        UUID event_id = UUIDs.timeBased();
        String event_type = "stop";

        long endTime= new Date().getTime();

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

        session.execute(query);

        currentFlight = "";

        return service.confirmation(racerName);
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/fail/")
    public String failRace() {
        UUID event_id = UUIDs.timeBased();

        String event_type = "fail";

        long endTime= new Date().getTime();

        PreparedStatement insertEvent = stmts.getInsertEvent();
        //"(id, racer_id , duration ,start_time, end_time , alt_avg , bat_avg , cam_avg , mode_avg , spd_avg , temp_height_avg , wifi_avg ) " +
        BoundStatement query = insertEvent.bind()
                .setString("id", racerName)
                .setUUID("event_id", event_id)
                .setString("event_type", event_type)
                .setLong("event_time", endTime);

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

        currentFlight = "";

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

        PreparedStatement insertEvent = stmts.getInsertEvent();
        BoundStatement query = insertEvent.bind()
                .setString("id", racerName)
                .setUUID("event_id", event_id)
                .setString("event_type", event_type)
                .setLong("event_time", startTime);
        session.execute(query);

        insertEvent = stmts.getInsertFlightEvents();
        query = insertEvent.bind()
                .setString("flight_id", currentFlight)
                .setUUID("event_uuid", event_id)
                .setString("event_type", event_type)
                .setLong("start_time", startTime);
        session.execute(query);

        insertEvent = stmts.getInsertFlight();
        query = insertEvent.bind()
                .setString("flight_id", currentFlight)
                .setString("racer_id", racerName)
                .setLong("start_time", startTime);
        session.execute(query);

        return service.confirmation(racerName);
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/event/")
    public Event insertEvent(Event event) {
        UUID event_id = UUIDs.timeBased();
        String event_type = "payload";

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
        session.execute(query);

        insertEvent = stmts.getInsertFlightEvents();
        query = insertEvent.bind()
                .setString("flight_id", currentFlight)
                .setUUID("event_uuid", event_id)
                .setString("event_type", event_type)
                .setLong("start_time", startTime);
        session.execute(query);

        return event;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/position/")
    public DronePosition insertPosition(DronePosition position) {
        UUID event_id = UUIDs.timeBased();
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
    @Path("/graph")
    @Produces(MediaType.APPLICATION_JSON)
    public String getGraph() {
        try {
            GraphTraversalSource g = DseGraph.traversal();
            GraphTraversal traversal = g.V().outE();

            GraphStatement graphStatement = DseGraph.statementFromTraversal(traversal);

            GraphResultSet output = session.executeGraph(graphStatement);

            List<GraphNode> nodes = output.all();

            return nodes.toString();

        }catch (Exception e){
            System.out.println(e.toString());
            e.printStackTrace();
            return null;
        }
    }


    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public List<GraphRepresentation.VertexRepresentation> getList(
            @QueryParam("vertexLabel") String vertexLabel,
            @QueryParam("vertexId") String vertexId,
            @QueryParam("property") String property,
            @QueryParam("count") int count,
            @QueryParam("max") int max,
            @QueryParam("min") int min) {
        GraphTraversalSource g = DseGraph.traversal(session);

        List<Vertex> vertices = g.V().hasLabel(vertexLabel).has(property, P.between(min, max)).limit(count).order().by(property, decr).toList();
        for (Vertex vertex : vertices) {
            GraphRepresentation.VertexRepresentation vertexR = new GraphRepresentation.VertexRepresentation();
            vertexR.setLabel(vertex.label());
            vertexR.setLabel(vertex.label());
        }

        List<GraphRepresentation.VertexRepresentation> vertexList = vertexListToGraphRepresentation(vertices);
        return vertexList;
    }

    private List<GraphRepresentation.VertexRepresentation> vertexListToGraphRepresentation(List<Vertex> vertices) {
        List<GraphRepresentation.VertexRepresentation> vertexList = new ArrayList();
        for (Vertex vertex : vertices) {
            GraphRepresentation.VertexRepresentation vertexR = new GraphRepresentation.VertexRepresentation();
            vertexR.setLabel(vertex.label());
            LinkedHashMap<String, String> complexId = (LinkedHashMap) vertex.id();
            for (Map.Entry<String, String> entry : complexId.entrySet()) {
                if (entry.getKey().equals("~label")){
                    vertexR.setLabel(entry.getValue());
                }else {
                    vertexR.setId(entry.getValue());
                }
            }

            Map<String, Object> properties = new HashMap<>();
            for (Iterator<VertexProperty<Object>> it = vertex.properties(); it.hasNext(); ) {
                VertexProperty property = it.next();
                properties.put(property.label(), property.value());
            }
            vertexR.setProperties(properties);
            vertexList.add(vertexR);
        }
        return vertexList;

    }


    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "hello";
    }
}
