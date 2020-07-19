package com.skobelev.payments.service;

import com.skobelev.payments.dao.PaymentRepository;
import com.skobelev.payments.dto.BillAggregateRequest;
import com.skobelev.payments.dto.Payment;
import com.skobelev.payments.model.UserBillAggregate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BillAggregateResponseService responseService;
    private final BillAggregateValidatorService validatorService;

    @Autowired
    public PaymentService(@NotNull final PaymentRepository paymentRepository,
                          @NotNull final BillAggregateResponseService responseService,
                          @NotNull final BillAggregateValidatorService validatorService) {
        this.paymentRepository = paymentRepository;
        this.responseService = responseService;
        this.validatorService = validatorService;
    }

    public void transfer(@NotNull final List<Payment> payments) {
        log.info("Have {} transfer data", payments.size());
        paymentRepository.add(payments);
    }

    public void billAggregate(@NotNull final BillAggregateRequest request) {
        log.info("To bill {}", request.getUsername());
        if (validatorService.validateBillRequest(request)) {
            UserBillAggregate aggregate = paymentRepository.billAggregate(request.getUsername());
            responseService.sendResponse(request, aggregate);
        }
    }
}
