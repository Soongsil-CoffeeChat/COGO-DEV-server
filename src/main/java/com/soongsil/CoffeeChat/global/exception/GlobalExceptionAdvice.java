package com.soongsil.CoffeeChat.global.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.core.JsonParseException;

@RestControllerAdvice(annotations = {RestController.class})
public class GlobalExceptionAdvice {

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<ErrorResponse> handleCommonException(GlobalException e) {
        return new ResponseEntity<>(
                new ErrorResponse(e.getGlobalErrorCode()), e.getGlobalErrorCode().getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e) {
        List<String> fieldErrors =
                e.getBindingResult().getAllErrors().stream()
                        .filter(error -> error instanceof FieldError)
                        .map(
                                error -> {
                                    FieldError fieldError = (FieldError) error;
                                    return fieldError.getField()
                                            + ": "
                                            + fieldError.getDefaultMessage();
                                })
                        .collect(Collectors.toList());

        String errorMessage = String.join(", ", fieldErrors);

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(GlobalErrorCode.BAD_REQUEST, errorMessage));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(GlobalErrorCode.BAD_REQUEST, e.getMessage()));
    }

    // MissingServletRequestParameterException: Missing required parameters
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameterException(
            MissingServletRequestParameterException e) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(GlobalErrorCode.BAD_REQUEST, e.getMessage()));
    }

    // JsonParseException: JSON structure errors
    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<ErrorResponse> handleJsonParseException(JsonParseException e) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(GlobalErrorCode.BAD_REQUEST, e.getMessage()));
    }

    // Handle all exceptions: Default 500 server error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(GlobalErrorCode.INTERNAL_SERVER_ERROR, e.getMessage()));
    }
}
