package com.bsm.tradenest.exception;

import com.bsm.tradenest.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> handleInvalidCredentials(
            InvalidCredentialsException ex) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(
                        "AUTH_FAILED",
                        ex.getMessage()
                ));
    }



    @ExceptionHandler(NotFoundException.class)
    public org.springframework.http.ResponseEntity<?> handleNotFound(
            NotFoundException ex) {

        return org.springframework.http.ResponseEntity
                .status(404)
                .body(new ErrorResponse("NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> badRequest(BadRequestException ex) {
        return ResponseEntity.badRequest()
                .body(ex.getMessage());
    }




}
