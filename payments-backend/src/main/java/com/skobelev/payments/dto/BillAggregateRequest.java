package com.skobelev.payments.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class BillAggregateRequest {
    @NotBlank
    private String username;

    private String url;

    private String topic;

    @NotNull
    private PayloadType payloadType;
}
