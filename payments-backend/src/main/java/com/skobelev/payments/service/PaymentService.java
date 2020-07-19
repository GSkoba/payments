package com.skobelev.payments.service;

import com.skobelev.payments.dao.PaymentRepository;
import com.skobelev.payments.dto.BillAggregateRequest;
import com.skobelev.payments.dto.PayloadType;
import com.skobelev.payments.dto.PaymentDto;
import com.skobelev.payments.model.UserBillAggregate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotNull;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;
    private final KafkaTemplate<String, UserBillAggregate> kafkaTemplate;

    @Autowired
    public PaymentService(@NotNull final PaymentRepository paymentRepository,
                          @NotNull final RestTemplate restTemplate,
                          @NotNull final KafkaTemplate<String, UserBillAggregate> kafkaTemplate) {
        this.paymentRepository = paymentRepository;
        this.restTemplate = restTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void transfer(@NotNull final List<PaymentDto> payments) {
        paymentRepository.add(payments);
    }

    public void billAggregate(@NotNull final BillAggregateRequest request) {
        validateBillRequest(request);
        UserBillAggregate aggregate = paymentRepository.billAggregate(request.getUsername());
        sendResponse(request, aggregate);
    }

    private void validateBillRequest(BillAggregateRequest request) {
        if (PayloadType.URL.equals(request.getPayloadType()) &&
                (request.getUrl() == null || request.getUrl().isEmpty())) {
            throw new IllegalStateException("Url in payload cant be empty");
        }
    }

    private void sendResponse(BillAggregateRequest request, UserBillAggregate aggregate) {
        switch (request.getPayloadType()) {
            case URL:
                sendResponse(request.getUrl(), aggregate);
                break;
            case TOPIC:
                kafkaTemplate.send(request.getTopic(), aggregate);
                break;
        }
    }

    private void sendResponse(String url, UserBillAggregate aggregate) {
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
}
