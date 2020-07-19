package com.skobelev.payments.service;

import com.skobelev.payments.dto.BillAggregateRequest;
import com.skobelev.payments.dto.PayloadType;
import org.springframework.stereotype.Service;

@Service
public class BillAggregateValidatorService {

    public boolean validateBillRequest(BillAggregateRequest request) {
        if (PayloadType.URL.equals(request.getPayloadType()) &&
                (request.getUrl() == null || request.getUrl().isEmpty())) {
            throw new IllegalArgumentException("Url in payload cant be empty");
        }
        if (PayloadType.TOPIC.equals(request.getPayloadType()) &&
                (request.getTopic() == null || request.getTopic().isEmpty())) {
            throw new IllegalArgumentException("Kafka topic in payload cant be empty");
        }
        return true;
    }
}
