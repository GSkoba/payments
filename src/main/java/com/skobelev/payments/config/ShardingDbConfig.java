package com.skobelev.payments.config;

import com.skobelev.payments.config.properties.ShardingDbProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class ShardingDbConfig {

    @Value("${sharding-db.init-script}")
    private String initScript;

    @Bean
    public List<DataSource> dataSources(ShardingDbProperties properties) {
        return properties.getConnections().stream().map(connection -> {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(connection.getUrl());
            config.setUsername(connection.getUsername());
            config.setPassword(connection.getPassword());
            config.setConnectionInitSql("create table if not exists payments(\n" +
                    "    id serial primary key not null,\n" +
                    "    user_from text,\n" +
                    "    user_to text,\n" +
                    "    money numeric\n" +
                    ");");
            return new HikariDataSource(config);
        }).collect(Collectors.toList());
    }
}
