package com.skobelev.payments.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@AllArgsConstructor
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Not valid data in request")
public class NotValidBillAggregateRequestException extends RuntimeException {
    private final String msg;
}
