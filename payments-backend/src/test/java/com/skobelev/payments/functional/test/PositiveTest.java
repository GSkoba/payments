package com.skobelev.payments.functional.test;

import com.skobelev.payments.dto.BillAggregateRequest;
import com.skobelev.payments.dto.TransferRequest;
import com.skobelev.payments.functional.config.ShardingDbTestConfig;
import com.skobelev.payments.extension.JsonData;
import com.skobelev.payments.extension.JsonExtensions;
import com.skobelev.payments.model.UserBillAggregate;
import lombok.SneakyThrows;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.integration.utils.IntegrationTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith({SpringExtension.class, JsonExtensions.class})
@SpringBootTest(classes = {ShardingDbTestConfig.class}, properties = {"application-test.yaml"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PositiveTest {

    @Autowired
    public TestRestTemplate restTemplate;

    @Autowired
    public List<DataSource> dataSources;

    @Autowired
    public ShardingDbTestConfig config;

    @Test
    @DisplayName("Push transfer data to server and check sharding")
    void transferTest(@JsonData("json_data/valid_transfer.json") TransferRequest transfer) {
        ResponseEntity<Void> response = restTemplate.postForEntity("/payment/transfer/", transfer, Void.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, countRow(dataSources.get(0)));
        assertEquals(1, countRow(dataSources.get(1)));
        assertEquals(1, countRow(dataSources.get(1)));
    }

    @Test
    @SneakyThrows
    @DisplayName("Send request for async data and wait response in kafka topic")
    void summarizeTest(@JsonData("json_data/summarize_request.json") BillAggregateRequest request) {
        ResponseEntity<Void> response = restTemplate.postForEntity("/payment/summarize/", request, Void.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<KeyValue<String, UserBillAggregate>> record =
                IntegrationTestUtils.waitUntilMinKeyValueRecordsReceived(config.getConsumer(), "billing", 1, 30_000);
        assertNotNull(record);
        assertFalse(record.isEmpty());
        UserBillAggregate aggregate = record.get(0).value;
        assertNotNull(aggregate);
        assertEquals("Vova", aggregate.getUsername());
        assertEquals(BigDecimal.valueOf(60), aggregate.getAggregateBill());
    }

    private int countRow(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            ResultSet rs = connection.createStatement().executeQuery("select count(*) from payments;");
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0;
    }
}
