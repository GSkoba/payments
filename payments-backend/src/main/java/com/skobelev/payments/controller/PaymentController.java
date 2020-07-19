package com.skobelev.payments.controller;

import com.skobelev.payments.dto.BillAggregateRequest;
import com.skobelev.payments.dto.TransferRequest;
import com.skobelev.payments.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(@NotNull final PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/transfer")
    public void moneyTransfer(@RequestBody @Valid TransferRequest payments) {
        paymentService.transfer(payments.getPayments());
    }

    @PostMapping("/summarize")
    public void test(@RequestBody @Valid BillAggregateRequest billAggregateRequest) {
        paymentService.billAggregate(billAggregateRequest);
    }
}
