CREATE KEYSPACE IF NOT EXISTS portal
    WITH REPLICATION = {
        'class' : 'SimpleStrategy',
        'replication_factor': 1
    }
    AND DURABLE_WRITES = true;
 

CREATE TABLE portal.spring_session (
    primary_id text PRIMARY KEY,
    creation_time text,
    expiry_time text,
    last_access_time text,
    max_inactive_interval text,
    principal_name text,
    session_id text,
    vector_ts text
) WITH bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': '10'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND dclocal_read_repair_chance = 0.1
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND read_repair_chance = 0.0
    AND speculative_retry = '99PERCENTILE';
    
  
CREATE TABLE portal.spring_session_attributes (
    primary_id text,
    attribute_name text,
    attribute_bytes blob,
    vector_ts text,
    PRIMARY KEY (primary_id, attribute_name)
) WITH CLUSTERING ORDER BY (attribute_name ASC)
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': '1'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND dclocal_read_repair_chance = 0.1
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND read_repair_chance = 0.0
    AND speculative_retry = '99PERCENTILE';  
    
    
CREATE TABLE portal.health_check (primary_id text PRIMARY KEY, creation_time text);
insert into portal.health_check (primary_id,creation_time) values ('ECOMPPortal-25927','2018-05-25T20:14:39.408Z');
