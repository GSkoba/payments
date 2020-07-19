package com.skobelev.payments.functional.config.properties;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DbConnection {

    @NotBlank
    private String url;

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
