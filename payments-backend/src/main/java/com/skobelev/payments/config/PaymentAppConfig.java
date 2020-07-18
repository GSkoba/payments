package com.skobelev.payments.config;

import com.skobelev.payments.dao.PaymentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.util.List;

@Configuration
public class PaymentAppConfig {

    @Bean
    public PaymentRepository paymentDao(@NotNull final List<DataSource> dataSources) {
        return new PaymentRepository(dataSources);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
