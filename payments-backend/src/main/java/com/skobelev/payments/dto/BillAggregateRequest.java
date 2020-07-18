package com.skobelev.payments.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BillAggregateRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String url;

    private PayloadType payloadType;
}
