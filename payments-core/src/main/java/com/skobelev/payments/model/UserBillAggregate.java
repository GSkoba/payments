package com.skobelev.payments.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBillAggregate {
    @NotBlank
    private String username;

    @Positive
    private BigDecimal aggregateBill;
}
