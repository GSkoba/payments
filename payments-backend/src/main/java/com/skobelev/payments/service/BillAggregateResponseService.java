package com.skobelev.payments.service;

import com.skobelev.payments.dto.BillAggregateRequest;
import com.skobelev.payments.exceptions.ServerNotFoundException;
import com.skobelev.payments.model.UserBillAggregate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotNull;

@Service
@Slf4j
public class BillAggregateResponseService {

    private final RestTemplate restTemplate;
    private final KafkaTemplate<String, UserBillAggregate> kafkaTemplate;

    @Autowired
    public BillAggregateResponseService(@NotNull final RestTemplate restTemplate,
                                        @NotNull final KafkaTemplate<String, UserBillAggregate> kafkaTemplate) {
        this.restTemplate = restTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendResponse(BillAggregateRequest request, UserBillAggregate aggregate) {
        switch (request.getPayloadType()) {
            case URL:
                sendResponseByUrl(request.getUrl(), aggregate);
                break;
            case TOPIC:
                sendResponseByKafkaTopic(request.getTopic(), aggregate);
                break;
        }
    }

    public void sendResponseByUrl(String url, UserBillAggregate aggregate) {
        log.info("Send data to remote server by url");
        ResponseEntity<Void> response;
        try {
            response = restTemplate.postForEntity(url,
                    new HttpEntity<>(aggregate), Void.class);
        } catch (Exception ex) {
            log.error("Bad request on url {}", url, ex);
            throw new ServerNotFoundException(String.format("Bad request on url %s", url));
        }
        if (!HttpStatus.OK.equals(response.getStatusCode())) {
            log.error("Bad request on url {}, status {}", url, response.getStatusCode());
            throw new ServerNotFoundException(String.format("Bad request on url %s", url));
        }
    }

    public void sendResponseByKafkaTopic(String topic, UserBillAggregate aggregate) {
        log.info("Send data to kafka topic");
        kafkaTemplate.send(topic, aggregate);
    }
}
