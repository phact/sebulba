CREATE KEYSPACE sebulba WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}  AND durable_writes = true AND graph_engine = 'Native';

CREATE TABLE sebulba.flight (
    flight_id text PRIMARY KEY,
    duration bigint,
    end_time bigint,
    race_status text,
    racer_id text,
    start_time bigint
) WITH VERTEX LABEL flight;

CREATE TABLE sebulba.topic (
    topic_id text PRIMARY KEY,
    topic_of_interest text
) WITH VERTEX LABEL topic;

CREATE TABLE sebulba.person (
    attendee_id text PRIMARY KEY,
    address_1 text,
    address_2 text,
    cell_phone text,
    company text,
    country text,
    email text,
    first_name text,
    job_function text,
    job_title text,
    last_name text,
    postal_code int,
    registration_type text,
    state text,
    work_phone text
) WITH VERTEX LABEL person;

CREATE TABLE sebulba.session (
    session_id text PRIMARY KEY,
    abstract text,
    keywords set<text>,
    session_name text,
    session_title text,
    session_type text,
    speaker_bio text,
    speaker_company text,
    speaker_title list<text>,
    track text
) WITH VERTEX LABEL session;

CREATE TABLE sebulba.flight_events (
    flight_id text,
    start_time bigint,
    event_uuid timeuuid,
    alt bigint,
    bat bigint,
    cam bigint,
    end_time bigint,
    event_type text,
    mode bigint,
    race_status text,
    spd bigint,
    temp_height bigint,
    wifi bigint,
    PRIMARY KEY (flight_id, start_time, event_uuid)
) WITH CLUSTERING ORDER BY (start_time ASC, event_uuid ASC)
    AND VERTEX LABEL flight_events;

CREATE TABLE sebulba.flight_telematics (
    flight_id text,
    time bigint,
    imu_acc_x double,
    imu_acc_y double,
    imu_acc_z double,
    imu_gyro_x double,
    imu_gyro_y double,
    imu_gyro_z double,
    imu_q0 double,
    imu_q1 double,
    imu_q2 double,
    imu_q3 double,
    imu_vg_x double,
    imu_vg_y double,
    imu_vg_z double,
    mvo_pos_x double,
    mvo_pos_y double,
    mvo_pos_z double,
    mvo_vel_x double,
    mvo_vel_y double,
    mvo_vel_z double,
    PRIMARY KEY (flight_id, time)
) WITH CLUSTERING ORDER BY (time ASC)
    AND VERTEX LABEL flight_telematics;

CREATE TABLE sebulba.person__interested_in__topic (
    person_attendee_id text,
    topic_topic_id text,
    PRIMARY KEY (person_attendee_id, topic_topic_id)
) WITH CLUSTERING ORDER BY (topic_topic_id ASC)
    AND EDGE LABEL interested_in FROM person(person_attendee_id) TO topic(topic_topic_id);

CREATE MATERIALIZED VIEW sebulba.interest_of AS
    SELECT *
    FROM sebulba.person__interested_in__topic
    WHERE topic_topic_id IS NOT NULL AND person_attendee_id IS NOT NULL
    PRIMARY KEY (topic_topic_id, person_attendee_id)
    WITH CLUSTERING ORDER BY (person_attendee_id ASC);

CREATE TABLE sebulba.person__works_with__person (
    out_attendee_id text,
    in_attendee_id text,
    PRIMARY KEY (out_attendee_id, in_attendee_id)
) WITH CLUSTERING ORDER BY (in_attendee_id ASC)
    AND EDGE LABEL works_with FROM person(out_attendee_id) TO person(in_attendee_id);

CREATE MATERIALIZED VIEW sebulba.works_with_reversed AS
    SELECT *
    FROM sebulba.person__works_with__person
    WHERE out_attendee_id IS NOT NULL AND in_attendee_id IS NOT NULL
    PRIMARY KEY (in_attendee_id, out_attendee_id)
    WITH CLUSTERING ORDER BY (out_attendee_id ASC);

CREATE TABLE sebulba.person__attended__session (
    person_attendee_id text,
    session_session_id text,
    PRIMARY KEY (person_attendee_id, session_session_id)
) WITH CLUSTERING ORDER BY (session_session_id ASC)
    AND EDGE LABEL attended FROM person(person_attendee_id) TO session(session_session_id);

CREATE MATERIALIZED VIEW sebulba.attendees AS
    SELECT *
    FROM sebulba.person__attended__session
    WHERE session_session_id IS NOT NULL AND person_attendee_id IS NOT NULL
    PRIMARY KEY (session_session_id, person_attendee_id)
    WITH CLUSTERING ORDER BY (person_attendee_id ASC);

CREATE TABLE sebulba.person__flew_in__flight (
    person_attendee_id text,
    flight_flight_id text,
    PRIMARY KEY (person_attendee_id, flight_flight_id)
) WITH CLUSTERING ORDER BY (flight_flight_id ASC)
    AND EDGE LABEL flew_in FROM person(person_attendee_id) TO flight(flight_flight_id);

CREATE MATERIALIZED VIEW sebulba.flew_in_reversed AS
    SELECT *
    FROM sebulba.person__flew_in__flight
    WHERE flight_flight_id IS NOT NULL AND person_attendee_id IS NOT NULL
    PRIMARY KEY (flight_flight_id, person_attendee_id)
    WITH CLUSTERING ORDER BY (person_attendee_id ASC);

CREATE TABLE sebulba.flight_events__included_in__flight (
    flight_events_flight_id text,
    flight_events_start_time bigint,
    flight_events_event_uuid timeuuid,
    flight_flight_id text,
    PRIMARY KEY (flight_events_flight_id, flight_events_start_time, flight_events_event_uuid, flight_flight_id)
) WITH CLUSTERING ORDER BY (flight_events_start_time ASC, flight_events_event_uuid ASC, flight_flight_id ASC)
    AND EDGE LABEL included_in FROM flight_events(flight_events_flight_id, flight_events_start_time, flight_events_event_uuid) TO flight(flight_flight_id);

CREATE TABLE sebulba.flight_telematics__included_in__flight (
    flight_telematics_flight_id text,
    flight_telematics_time bigint,
    flight_flight_id text,
    PRIMARY KEY (flight_telematics_flight_id, flight_telematics_time, flight_flight_id)
) WITH CLUSTERING ORDER BY (flight_telematics_time ASC, flight_flight_id ASC)
    AND EDGE LABEL included_in FROM flight_telematics(flight_telematics_flight_id, flight_telematics_time) TO flight(flight_flight_id);
