package com.eliascanalesnieto.foodtracker.exception;

import com.eliascanalesnieto.foodtracker.dto.out.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatusCode;
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

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ErrorResponse> handle(final AuthorizationException authorizationException) {
        return ResponseEntity.status(HttpStatusCode.valueOf(403)).body(new ErrorResponse(authorizationException.getMessage()));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handle(final ExpiredJwtException authorizationException) {
        return ResponseEntity.status(HttpStatusCode.valueOf(403)).body(new ErrorResponse("Token error"));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(final EntityNotFoundException authorizationException) {
        return ResponseEntity.notFound().build();
    }

}
