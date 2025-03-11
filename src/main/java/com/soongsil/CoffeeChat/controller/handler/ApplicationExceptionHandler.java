package com.soongsil.CoffeeChat.controller.handler;

import static com.soongsil.CoffeeChat.controller.handler.ApiResponseGenerator.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import com.soongsil.CoffeeChat.controller.ApplicationController;
import com.soongsil.CoffeeChat.controller.exception.CustomException;

@ControllerAdvice(assignableTypes = ApplicationController.class)
public class ApplicationExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponseGenerator> handleCustomException(CustomException ex) {
        return ResponseEntity.status(HttpStatus.valueOf(ex.getErrorCode().value()))
                .body(onFailure(ex));
    }
}
