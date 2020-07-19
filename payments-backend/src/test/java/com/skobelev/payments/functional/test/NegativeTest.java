package com.skobelev.payments.functional.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

import java.math.BigDecimal;
import java.util.List;

@ExtendWith({SpringExtension.class, JsonExtensions.class})
@SpringBootTest(classes = {ShardingDbTestConfig.class}, properties = {"application-test.yaml"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NegativeTest {

    @Autowired
    public TestRestTemplate restTemplate;

    @Autowired
    public ShardingDbTestConfig config;

    @Test
    @DisplayName("Push transfer not valid data")
    void transferTest(@JsonData("json_data/not_valid_transfer.json") TransferRequest request) {
        ResponseEntity<Void> response = restTemplate.postForEntity("/payment/transfer/", request, Void.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @SneakyThrows
    @DisplayName("Send request for async data and wait response in kafka topic by user without bill")
    void summarizeTest(@JsonData("json_data/not_valid_summarize_request.json") BillAggregateRequest request) {
        ResponseEntity<Void> response = restTemplate.postForEntity("/payment/summarize/", request, Void.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<KeyValue<String, UserBillAggregate>> record =
                IntegrationTestUtils.waitUntilMinKeyValueRecordsReceived(config.getConsumer(), "billing", 1, 30_000);
        assertNotNull(record);
        assertFalse(record.isEmpty());
        UserBillAggregate aggregate = record.get(0).value;
        assertNotNull(aggregate);
        assertEquals("Kseniy", aggregate.getUsername());
        assertEquals(BigDecimal.valueOf(0), aggregate.getAggregateBill());
    }
}
