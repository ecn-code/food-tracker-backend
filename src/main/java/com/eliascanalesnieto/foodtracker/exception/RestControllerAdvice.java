package com.eliascanalesnieto.foodtracker.exception;

import com.eliascanalesnieto.foodtracker.dto.out.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice(annotations = RestController.class)
public class RestControllerAdvice {

    @ExceptionHandler(LoginException.class)
    public ResponseEntity<ErrorResponse> handle(final LoginException loginException) {
        return ResponseEntity.badRequest().body(new ErrorResponse(loginException.getMessage()));
    }

}
