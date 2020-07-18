package com.skobelev.payments.controller;

import com.skobelev.payments.dto.TransferRequest;
import com.skobelev.payments.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/transfer")
    public void moneyTransfer(@RequestBody @Valid TransferRequest payments) {
        payments.getPayments().forEach(System.out::println);
        paymentService.transfer(payments.getPayments());
    }

    @GetMapping("/test")
    public void test() {
        String hello = "hello friend";
        restTemplate.postForEntity("http://localhost:8081/test", new HttpEntity<String>(hello), Void.class);
    }
}
