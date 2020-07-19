package com.skobelev.payments;

import com.skobelev.payments.functional.config.properties.ShardingDbProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ShardingDbProperties.class})
public class PaymentsApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(PaymentsApplication.class);
        app.addListeners(getApplicationPidFileWriter());
        app.run(args);
    }

    private static ApplicationPidFileWriter getApplicationPidFileWriter() {
        ApplicationPidFileWriter writer = new ApplicationPidFileWriter();
        writer.setTriggerEventType(ApplicationStartedEvent.class);
        return writer;
    }
}
