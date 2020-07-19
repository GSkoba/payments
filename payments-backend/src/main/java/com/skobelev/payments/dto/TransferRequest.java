package com.skobelev.payments.dto;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Validated
public class TransferRequest {

    @Valid
    @NotEmpty(message = "Input payments list cannot be empty.")
    private List<Payment> payments;
}
