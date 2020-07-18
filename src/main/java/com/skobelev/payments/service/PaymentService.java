package com.skobelev.payments.service;

import com.skobelev.payments.dao.PaymentRepository;
import com.skobelev.payments.dto.PaymentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public void transfer(List<PaymentDto> payments) {
        paymentRepository.add(payments);
    }
}
