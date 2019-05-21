package com.datastax.powertools;

import com.datastax.driver.core.*;
import com.datastax.driver.core.utils.UUIDs;
import com.datastax.driver.dse.DseSession;
import com.datastax.dse.graph.api.DseGraph;
import com.datastax.powertools.api.*;
import com.datastax.powertools.managed.DSEManager;
import com.datastax.powertools.managed.DSEStmts;
import com.datastax.shaded.jackson.core.JsonProcessingException;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.apache.tinkerpop.gremlin.structure.util.detached.DetachedVertex;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

import static org.apache.tinkerpop.gremlin.process.traversal.Order.decr;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.unfold;

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
    @Path("/graph")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, List> getGraph() throws JsonProcessingException {
        GraphTraversalSource g = DseGraph.traversal(session);

        Map<String, List> response = new HashMap<>();

        List<Map<Object, Object>> vertices = g.V().hasLabel("person", "session", "flight", "topic").valueMap(true).by(unfold()).toList();

        List<Edge> edges = g.E().hasLabel("flew_in", "interested_in", "works_with", "attended").toList();
        List<Map<String, Object>> edgeList = new ArrayList();
        for (Edge edge: edges) {
            Map<String, Object> edgeR = new HashMap<>();
            edgeR.put("source", edge.outVertex().id().toString().split(":|#")[1]);
            edgeR.put("target", edge.inVertex().id().toString().split(":|#")[1]);
            edgeR.put("id",edge.id().toString());
            edgeList.add(edgeR);
        }

        response.put("vertexList", vertices);
        response.put("edgeList", edgeList);
        return response;
    }



    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public List<VertexRepresentation> getList(
            @QueryParam("vertexLabel") String vertexLabel,
            @QueryParam("vertexId") String vertexId,
            @QueryParam("property") String property,
            @QueryParam("count") long count,
            @QueryParam("max") long max,
            @QueryParam("min") long min) {
        GraphTraversalSource g = DseGraph.traversal(session);

        List<Vertex> vertices = g.V().hasLabel(vertexLabel).has(property, P.between(min, max)).limit(count).order().by(property, decr).toList();
        for (Vertex vertex : vertices) {
            VertexRepresentation vertexR = new VertexRepresentation();
            vertexR.setLabel(vertex.label());
            vertexR.setLabel(vertex.label());
        }

        List<VertexRepresentation> vertexList = vertexListToGraphRepresentation(vertices);
        return vertexList;
    }

    private List<EdgeRepresentation> edgeListToGraphRepresentation(List<Edge> edges) {
        List<EdgeRepresentation> edgeList = new ArrayList();
        for (Edge edge: edges) {
            EdgeRepresentation edgeR = new EdgeRepresentation();
            edgeR.setLabel(edge.label());
            edgeR.setId(edge.id().toString());

            Map<String, Object> properties = new HashMap<>();
            edgeR.setSource(edge.inVertex().id().toString().split(":|#")[1]);
            edgeR.setTarget(edge.outVertex().id().toString().split(":|#")[1]);
            edgeList.add(edgeR);
        }
        return edgeList;


    }
    private List<VertexRepresentation> vertexListToGraphRepresentation(List<Vertex> vertices) {
        List<VertexRepresentation> vertexList = new ArrayList();
        for (Vertex vertex : vertices) {
            VertexRepresentation vertexR = new VertexRepresentation();
            vertexR.setLabel(vertex.label());
            String[] complexId = ((DetachedVertex) vertex).id().toString().split(":|#");
            //for (Map.Entry<String, String> entry : complexId) {
            vertexR.setLabel(complexId[0]);
            vertexR.setId(complexId[1]);

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
