
package shardingTest.lab.mysql.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() {
        ShardingRoutingDataSource routingDataSource = new ShardingRoutingDataSource();

        // Shard 1
        DataSource shard1 = DataSourceBuilder.create()
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .url("jdbc:mysql://localhost:3306/shard1_db")
                .username("shard1_user")
                .password("shard1_user_password")
                .build();

        // Shard 2
        DataSource shard2 = DataSourceBuilder.create()
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .url("jdbc:mysql://localhost:3307/shard2_db")
                .username("shard2_user")
                .password("shard2_user_password")
                .build();

        // Shard Mapping
        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("shard1", shard1);
        dataSourceMap.put("shard2", shard2);

        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(shard1);

        return routingDataSource;
    }
}