create KEYSPACE sebulba WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1} ;

create TABLE sebulba.events_by_racer
(
    id text,
    event_id bigint,
    event_uuid timeuuid,
    event_type text,
    start_time bigint,
    end_time bigint,
    alt bigint,
    bat bigint,
    cam bigint,
    mode bigint,
    spd bigint,
    temp_height bigint,
    wifi bigint,
    primary KEY ( id, event_uuid)
);
