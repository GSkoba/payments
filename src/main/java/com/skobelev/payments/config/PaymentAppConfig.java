package com.skobelev.payments.config;

import com.skobelev.payments.dao.PaymentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.List;

@Configuration
public class PaymentAppConfig {

    @Bean
    public PaymentRepository paymentDao(List<DataSource> dataSources) {
        return new PaymentRepository(dataSources);
    }
}
