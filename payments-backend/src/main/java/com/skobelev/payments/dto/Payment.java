package com.skobelev.payments.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @NotBlank
    private String from;

    @NotBlank
    private String to;

    @NotNull
    @Positive
    private BigDecimal money;
}
