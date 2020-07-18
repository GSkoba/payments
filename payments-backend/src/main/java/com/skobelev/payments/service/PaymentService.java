package com.skobelev.payments.service;

import com.skobelev.payments.dao.PaymentRepository;
import com.skobelev.payments.dto.PaymentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(@NotNull final PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public void transfer(@NotNull final List<PaymentDto> payments) {
        paymentRepository.add(payments);
    }
}
