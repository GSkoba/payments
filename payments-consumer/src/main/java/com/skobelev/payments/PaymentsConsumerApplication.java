package com.skobelev.payments;

import com.skobelev.payments.model.UserBillAggregate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class PaymentsConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentsConsumerApplication.class, args);
    }
}

@RestController
class PaymentConsumerController {

    @PostMapping("/test")
    public void test(@RequestBody UserBillAggregate test) {
        System.out.println(test);
    }
}

@Configuration
class Config {

    @KafkaListener(id = "myGroup", topics = {"billing"})
    public void listener(UserBillAggregate msg) {
        System.out.println(msg);
    }
}


