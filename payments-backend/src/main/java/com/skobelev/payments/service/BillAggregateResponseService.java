package com.skobelev.payments.service;

import com.skobelev.payments.dto.BillAggregateRequest;
import com.skobelev.payments.model.UserBillAggregate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotNull;

@Service
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
        ResponseEntity<Void> response;
        try {
            response = restTemplate.postForEntity(url,
                    new HttpEntity<>(aggregate), Void.class);
            if (!HttpStatus.OK.equals(response.getStatusCode())) {
                throw new IllegalStateException(String.format("Bad Request on url %s", url));
            }
        } catch (Exception ex) {
            throw new IllegalStateException(String.format("Bad Request on url %s", url));
        }
    }

    public void sendResponseByKafkaTopic(String topic, UserBillAggregate aggregate) {
        kafkaTemplate.send(topic, aggregate);
    }
}
