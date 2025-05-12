package com.sii.charityBoxes.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CollectionBoxNotFoundException.class)
    public ResponseEntity<?> handleCollectionBoxNotFoundException(CollectionBoxNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(FundraisingEventNotFound.class)
    public ResponseEntity<?> handleFundraisingEventNotFound(FundraisingEventNotFound e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalStateException(IllegalStateException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(InvalidCurrencyException.class)
    public ResponseEntity<?> handleInvalidCurrencyException(InvalidCurrencyException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMsg = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + " - " + fe.getDefaultMessage())
                .findFirst()
                .orElse("Error");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMsg);
    }
}
