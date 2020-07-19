package com.skobelev.payments.service;

import com.skobelev.payments.dto.BillAggregateRequest;
import com.skobelev.payments.dto.PayloadType;
import com.skobelev.payments.exceptions.NotValidBillAggregateRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BillAggregateValidatorService {

    public boolean validateBillRequest(BillAggregateRequest request) {
        if (PayloadType.URL.equals(request.getPayloadType()) &&
                (request.getUrl() == null || request.getUrl().isEmpty())) {
            log.error("Url in payload cant be empty");
            throw new NotValidBillAggregateRequestException("Url in payload cant be empty");
        }
        if (PayloadType.TOPIC.equals(request.getPayloadType()) &&
                (request.getTopic() == null || request.getTopic().isEmpty())) {
            log.error("Kafka topic in payload cant be empty");
            throw new NotValidBillAggregateRequestException("Kafka topic in payload cant be empty");
        }
        return true;
    }
}
