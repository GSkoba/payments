package com.skobelev.payments.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class PaymentDto {
    @NotBlank
    private final String from;

    @NotBlank
    private final String to;

    @NotNull
    @Positive
    private final BigDecimal money;
}
