package com.skobelev.payments.functional.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration
public class ShardingDbTestConfig {

    @Bean(destroyMethod = "close")
    public PostgreSQLContainer firstPostgresContainer() {
        PostgreSQLContainer container =
                new PostgreSQLContainer("postgres:12")
                .withPassword("secret")
                .withUsername("postgres");
        container.start();
        System.setProperty("sharding-db.connections[0].url", container.getJdbcUrl());
        return container;
    }

    @Bean(destroyMethod = "close")
    public PostgreSQLContainer secondPostgresContainer() {
        PostgreSQLContainer container =
                new PostgreSQLContainer("postgres:12")
                        .withPassword("secret")
                        .withUsername("postgres");
        container.start();
        System.setProperty("sharding-db.connections[1].url", container.getJdbcUrl());
        return container;
    }

    @Bean(destroyMethod = "close")
    public PostgreSQLContainer thirdPostgresContainer() {
        PostgreSQLContainer container =
                new PostgreSQLContainer("postgres:12")
                        .withPassword("secret")
                        .withUsername("postgres");
        container.start();
        System.setProperty("sharding-db.connections[2].url", container.getJdbcUrl());
        return container;
    }
}
