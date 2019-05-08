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
    PRIMARY KEY ( id, start_time, event_uuid)
)
WITH CLUSTERING ORDER BY ( start_time desc)
;

create Table sebulba.position_by_racer
(
  id text,
  time bigint,
  mvo_vel_x double,
  mvo_vel_y double,
  mvo_vel_z double,
  mvo_pos_x double,
  mvo_pos_y double,
  mvo_pos_z double,
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
  PRIMARY KEY (id, time)
)
WITH CLUSTERING ORDER BY ( time desc)
;

create TABLE sebulba.races_by_duration
(
    id text,
    racer_id text,
	race_status text,
    duration bigint,
    start_time bigint,
    end_time bigint,
    alt_avg bigint,
    bat_avg bigint,
    cam_avg bigint,
    mode_avg bigint,
    spd_avg bigint,
    temp_height_avg bigint,
    wifi_avg bigint,
    PRIMARY KEY ( racer_id, duration, id)
)
WITH CLUSTERING ORDER BY ( duration desc)
;
