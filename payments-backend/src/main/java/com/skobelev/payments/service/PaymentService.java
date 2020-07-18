package com.skobelev.payments.service;

import com.skobelev.payments.dao.PaymentRepository;
import com.skobelev.payments.dto.BillAggregateRequest;
import com.skobelev.payments.dto.PaymentDto;
import com.skobelev.payments.model.UserBillAggregate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotNull;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public PaymentService(@NotNull final PaymentRepository paymentRepository,
                          @NotNull final RestTemplate restTemplate) {
        this.paymentRepository = paymentRepository;
        this.restTemplate = restTemplate;
    }

    public void transfer(@NotNull final List<PaymentDto> payments) {
        paymentRepository.add(payments);
    }

    public void billAggregate(@NotNull final BillAggregateRequest request) {
        UserBillAggregate aggregate = paymentRepository.billAggregate(request.getUsername());
        sendResponse(request, aggregate);
    }

    private void sendResponse(BillAggregateRequest request, UserBillAggregate aggregate) {
        switch (request.getPayloadType()) {
            case URL:
                restTemplate.postForEntity(request.getUrl(),
                        new HttpEntity<>(aggregate), Void.class);
                break;
            case TOPIC:

        }
    }
}
