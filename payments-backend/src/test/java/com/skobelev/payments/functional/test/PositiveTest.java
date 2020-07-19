package com.skobelev.payments.functional.test;

import com.skobelev.payments.config.properties.ShardingDbProperties;
import com.skobelev.payments.functional.config.ShardingDbTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest(properties = {"application-test.yaml"})
@Import({ShardingDbTestConfig.class})
public class PositiveTest {

    @Autowired
    public ShardingDbProperties properties;

    @Test
    void test() {
        System.out.println("Test");
    }
}
