package com.skobelev.payments.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class UserBillAggregate {
    @NotBlank
    private String username;

    @Positive
    private BigDecimal aggregateBill;
}
