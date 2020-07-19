package com.skobelev.payments.functional.config;

import static org.apache.kafka.clients.admin.AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG;

import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.test.TestUtils;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.annotation.PreDestroy;
import java.util.Collections;
import java.util.Properties;

@TestConfiguration
public class ShardingDbTestConfig {

    private static final String PASSWORD = "secret";
    private static final String USERNAME = "postgres";
    private static final String DOCKER_IMAGE_VERSION = "postgres:12";

    private static final PostgreSQLContainer<?> firstContainer =
            new PostgreSQLContainer<>(DOCKER_IMAGE_VERSION)
                    .withPassword(PASSWORD)
                    .withUsername(USERNAME);

    private static final PostgreSQLContainer<?> secondContainer =
            new PostgreSQLContainer<>(DOCKER_IMAGE_VERSION)
                    .withPassword(PASSWORD)
                    .withUsername(USERNAME);

    private static final PostgreSQLContainer<?> thirdContainer =
            new PostgreSQLContainer<>(DOCKER_IMAGE_VERSION)
                    .withPassword(PASSWORD)
                    .withUsername(USERNAME);

    private static final KafkaContainer kafkaContainer =
            new KafkaContainer();

    @Getter
    private final Properties consumer;

    @SneakyThrows
    public ShardingDbTestConfig() {
        start(firstContainer, secondContainer, thirdContainer);
        kafkaContainer.start();
        createKafkaTopic();
        consumer = configConsumer();
        setSystemProperty();
    }

    private Properties configConsumer() {
        Properties consumer = TestUtils.consumerConfig(kafkaContainer.getBootstrapServers(),
                StringDeserializer.class, JsonDeserializer.class);
        consumer.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return consumer;
    }

    private void start(PostgreSQLContainer<?>... containers) {
        for (PostgreSQLContainer<?> container : containers) {
            container.start();
        }
    }

    private void createKafkaTopic() {
        NewTopic topic = new NewTopic("billing", 1, (short) 1);
        Properties properties = new Properties();
        properties.put(BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        try (AdminClient adminClient = AdminClient.create(properties)) {
            adminClient.createTopics(Collections.singleton(topic));
        }
    }

    private void setSystemProperty() {
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
}
