package com.skobelev.payments.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(ServerNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleSeverNotFoundException(final ServerNotFoundException ex) {
        return new ErrorResponse(ex.getMsg());
    }

    @ExceptionHandler(NotValidBillAggregateRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleNotValidBillAggregateException(final NotValidBillAggregateRequestException ex) {
        return new ErrorResponse(ex.getMsg());
    }

    @ExceptionHandler(TransferPaymentsException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleTransferPaymentsException(final TransferPaymentsException ex) {
        return new ErrorResponse(ex.getMsg());
    }

    @ExceptionHandler(SummarizeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleSummarizeException(final SummarizeException ex) {
        return new ErrorResponse(ex.getMsg());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException ex) {
        return new ErrorResponse("Not valid data in the request, see example in README.md");
    }
}
