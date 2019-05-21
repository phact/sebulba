:remote config alias g sebulba.g

system.graph("sebulba").
    ifNotExists().
    withReplication("{'class': 'SimpleStrategy', 'replication_factor': 1}").
    create()

schema.vertexLabel('flight').
    ifNotExists().
    partitionBy('flight_id', Text).
    property('racer_id', Text).
    property('race_status', Text).
    property('duration', Bigint).
    property('start_time', Bigint).
    property('end_time', Bigint).
    create()

schema.vertexLabel('flight_events').
    ifNotExists().
    partitionBy('flight_id', Text).
    clusterBy('start_time', Bigint).
    clusterBy('event_uuid', Timeuuid).
    property('event_type', Text).
    property('race_status', Text).
    property('end_time', Bigint).
    property('alt', Bigint).
    property('bat', Bigint).
    property('cam', Bigint).
    property('mode', Bigint).
    property('spd', Bigint).
    property('temp_height', Bigint).
    property('wifi', Bigint).
    create()

schema.edgeLabel('included_in').
    ifNotExists().
    from('flight_events').to('flight').
    create()

schema.vertexLabel('flight_telematics').
    ifNotExists().
    partitionBy('flight_id', Text).
    clusterBy('time', Bigint).
    property('mvo_vel_x', Double).
    property('mvo_vel_y', Double).
    property('mvo_vel_z', Double).
    property('mvo_pos_x', Double).
    property('mvo_pos_y', Double).
    property('mvo_pos_z', Double).
    property('imu_acc_x', Double).
    property('imu_acc_y', Double).
    property('imu_acc_z', Double).
    property('imu_gyro_x', Double).
    property('imu_gyro_y', Double).
    property('imu_gyro_z', Double).
    property('imu_q0', Double).
    property('imu_q1', Double).
    property('imu_q2', Double).
    property('imu_q3', Double).
    property('imu_vg_x', Double).
    property('imu_vg_y', Double).
    property('imu_vg_z', Double).
    create()

schema.edgeLabel('included_in').
    ifNotExists().
    from('flight_telematics').to('flight').
    create()

schema.vertexLabel('person').
    ifNotExists().
    partitionBy('attendee_id', Text).
    property('registration_type', Text).
    property('email', Text).
    property('first_name', Text).
    property('last_name', Text).
    property('job_title', Text).
    property('company', Text).
    property('address_1', Text).
    property('address_2', Text).
    property('state', Text).
    property('country', Text).
    property('postal_code', Int).
    property('work_phone', Text).
    property('cell_phone', Text).
    property('job_function', Text).
    create()

schema.edgeLabel('works_with').
    ifNotExists().
    from('person').to('person').
    create()

schema.edgeLabel('works_with').
    from('person').to('person').
    materializedView('works_with_reversed').
    ifNotExists().
    partitionBy('in_attendee_id').
    clusterBy('out_attendee_id').
    create()

schema.edgeLabel('flew_in').
    ifNotExists().
    from('person').to('flight').
    create()

schema.edgeLabel('flew_in').
    from('person').to('flight').
    materializedView('flew_in_reversed').
    ifNotExists().
    partitionBy('flight_flight_id').
    clusterBy('person_attendee_id').
    create()

schema.vertexLabel('session').
    ifNotExists().
    partitionBy('session_id', Text).
    property('session_title', Text).
    property('session_name', Text).
    property('speaker_title', listOf(Text)).
    property('speaker_company', Text).
    property('speaker_bio', Text).
    property('abstract', Text).
    property('track', Text).
    property('session_type', Text).
    property('keywords', setOf(Text)).
    create()

schema.edgeLabel('attended').
    ifNotExists().
    from('person').to('session').
    create()

schema.edgeLabel('attended').
    from('person').to('session').
    materializedView('attendees').
    ifNotExists().
    partitionBy('session_session_id').
    clusterBy('person_attendee_id').
    create()

schema.vertexLabel('topic').
    ifNotExists().
    partitionBy('topic_id', Text).
    property('topic_of_interest', Text).
    create()

schema.edgeLabel('interested_in').
    ifNotExists().
    from('person').to('topic').
    create()

schema.edgeLabel('interested_in').
    from('person').to('topic').
    materializedView('interest_of').
    ifNotExists().
    partitionBy('topic_topic_id').
    clusterBy('person_attendee_id').
    create()

schema.vertexLabel('company').
    ifNotExists().
    partitionBy('name', Text).
    create()

schema.edgeLabel('worksFor').
    ifNotExists().
    from('person').to('company').
    create()
