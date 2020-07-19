package com.skobelev.payments.functional.config;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.shaded.com.google.common.io.Resources;

import javax.annotation.PreDestroy;
import java.io.IOException;

@TestConfiguration
public class ShardingDbTestConfig {

    private static final PostgreSQLContainer<?> firstContainer =
            new PostgreSQLContainer<>("postgres:12")
                    .withPassword("secret")
                    .withUsername("postgres");

    private static final PostgreSQLContainer<?> secondContainer =
            new PostgreSQLContainer<>("postgres:12")
                    .withPassword("secret")
                    .withUsername("postgres");

    private static final PostgreSQLContainer<?> thirdContainer =
            new PostgreSQLContainer<>("postgres:12")
                    .withPassword("secret")
                    .withUsername("postgres");

    private static final KafkaContainer kafkaContainer =
            new KafkaContainer();

    @SneakyThrows
    public ShardingDbTestConfig() {
        firstContainer.start();
        secondContainer.start();
        thirdContainer.start();
        kafkaContainer.start();
        setContainerProperty();
    }

    private void setContainerProperty() {
        System.setProperty("sharding-db.connections[0].url", firstContainer.getJdbcUrl());
        System.setProperty("sharding-db.connections[0].username", firstContainer.getUsername());
        System.setProperty("sharding-db.connections[0].password", firstContainer.getPassword());
        System.setProperty("sharding-db.connections[1].url", secondContainer.getJdbcUrl());
        System.setProperty("sharding-db.connections[1].username", secondContainer.getUsername());
        System.setProperty("sharding-db.connections[1].password", secondContainer.getPassword());
        System.setProperty("sharding-db.connections[2].url", thirdContainer.getJdbcUrl());
        System.setProperty("sharding-db.connections[2].username", thirdContainer.getUsername());
        System.setProperty("sharding-db.connections[2].password", thirdContainer.getPassword());
        System.setProperty("spring.kafka.bootstrap-servers", kafkaContainer.getBootstrapServers());
    }

    @PreDestroy
    public void close() {
        firstContainer.close();
        secondContainer.close();
        thirdContainer.close();
        kafkaContainer.start();
    }
//
//    @Bean(destroyMethod = "close")
//    public PostgreSQLContainer<?> firstPostgresContainer() {
//        PostgreSQLContainer<?> container =
//                new PostgreSQLContainer<>("postgres:12")
//                .withPassword("secret")
//                .withUsername("postgres");
//        container.start();
//        System.setProperty("sharding-db.connections[0].url", container.getJdbcUrl());
//        return container;
//    }
//
//    @Bean(destroyMethod = "close")
//    public PostgreSQLContainer<?> secondPostgresContainer() {
//        PostgreSQLContainer<?> container =
//                new PostgreSQLContainer<>("postgres:12")
//                        .withPassword("secret")
//                        .withUsername("postgres");
//        container.start();
//        System.setProperty("sharding-db.connections[1].url", container.getJdbcUrl());
//        return container;
//    }
//
//    @Bean(destroyMethod = "close")
//    public PostgreSQLContainer<?> thirdPostgresContainer() {
//        PostgreSQLContainer<?> container =
//                new PostgreSQLContainer<>("postgres:12")
//                        .withPassword("secret")
//                        .withUsername("postgres");
//        container.start();
//        System.setProperty("sharding-db.connections[2].url", container.getJdbcUrl());
//        return container;
//    }
}
