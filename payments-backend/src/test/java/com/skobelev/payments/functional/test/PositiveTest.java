package com.skobelev.payments.functional.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.skobelev.payments.dto.TransferRequest;
import com.skobelev.payments.functional.config.properties.ShardingDbProperties;
import com.skobelev.payments.functional.extension.JsonData;
import com.skobelev.payments.functional.extension.JsonExtensions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.skobelev.payments.functional.config.ShardingDbTestConfig;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@ExtendWith({SpringExtension.class, JsonExtensions.class})
@SpringBootTest(classes = {ShardingDbTestConfig.class}, properties = {"application-test.yaml"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PositiveTest {

    @Autowired
    public ShardingDbProperties properties;

    @Autowired
    public TestRestTemplate restTemplate;

    @Autowired
    public List<DataSource> dataSources;

//    @AfterEach
//    public void clear() {
//        dataSources.forEach(dataSource -> {
//            try (Connection connection = dataSource.getConnection()) {
//                connection.createStatement().execute("truncate table payments;");
//            } catch (SQLException throwables) {
//                throwables.printStackTrace();
//            }
//        });
//    }

    @Test
    void test(@JsonData("valid_transfer.json") TransferRequest transfer) {
        ResponseEntity<Void> response = restTemplate.postForEntity("/payment/transfer/", transfer, Void.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, countRow(dataSources.get(0)));
        assertEquals(1, countRow(dataSources.get(1)));
        assertEquals(1, countRow(dataSources.get(1)));
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
