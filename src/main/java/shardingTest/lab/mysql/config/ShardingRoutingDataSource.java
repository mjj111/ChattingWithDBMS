package shardingTest.lab.mysql.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class ShardingRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        Long roomId = ThreadLocalDatabaseContextHolder.popRoomId();

        if (roomId == null) {
            return "shard2";
        }

        //Range Sharding
        if (roomId <= 18000) {
            return "shard1";
        } else {
            return "shard2";
        }
    }
}