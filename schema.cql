create KEYSPACE sebulba WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1} ;

create TABLE sebulba.events_by_racer
( id text,
event_id timeuuid,
event_type text static,
name text,
start_time bigint,
end_time bigint,
primary KEY ( id, event_id)
);