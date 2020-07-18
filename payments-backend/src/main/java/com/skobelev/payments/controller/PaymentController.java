package com.skobelev.payments.controller;

import com.skobelev.payments.dto.BillAggregateRequest;
import com.skobelev.payments.dto.TransferRequest;
import com.skobelev.payments.model.UserBillAggregate;
import com.skobelev.payments.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    public PaymentController(@NotNull final PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/transfer")
    public void moneyTransfer(@RequestBody @Valid TransferRequest payments) {
        payments.getPayments().forEach(System.out::println);
        paymentService.transfer(payments.getPayments());
    }

    @PostMapping("/aggregate")
    public void test(@RequestBody @Valid BillAggregateRequest billAggregateRequest) {
        UserBillAggregate aggregate = new UserBillAggregate();
        aggregate.setUsername(billAggregateRequest.getUsername());
        aggregate.setAggregateBill(BigDecimal.valueOf(100));
        restTemplate.postForEntity(billAggregateRequest.getUrl(),
                new HttpEntity<UserBillAggregate>(aggregate), Void.class);
    }
}
