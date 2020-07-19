package com.skobelev.payments.service;

import com.skobelev.payments.dao.PaymentRepository;
import com.skobelev.payments.dto.BillAggregateRequest;
import com.skobelev.payments.dto.Payment;
import com.skobelev.payments.model.UserBillAggregate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;

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
        paymentRepository.add(payments);
    }

    public void billAggregate(@NotNull final BillAggregateRequest request) {
        validatorService.validateBillRequest(request);
        UserBillAggregate aggregate = paymentRepository.billAggregate(request.getUsername());
        responseService.sendResponse(request, aggregate);
    }
}
